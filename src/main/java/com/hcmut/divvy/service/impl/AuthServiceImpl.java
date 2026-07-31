package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.repository.PasswordResetTokenRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.security.JwtTokenProvider;
import com.hcmut.divvy.service.AuthService;
import com.hcmut.divvy.service.EmailService;
import com.hcmut.divvy.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.reset-password.expiry-minutes:30}")
    private int resetTokenExpiryMinutes;

    @Value("${app.reset-password.base-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    // ── Existing methods ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse register(CreateUserRequest request) {
        userValidator.validateCreateUser(request);

        User user = userMapper.toEntity(request);
        user.setHashPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        String token = tokenProvider.generateToken(saved.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userMapper.toResponse(saved))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", request.getUsernameOrEmail())));

        String token = tokenProvider.generateToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return userMapper.toResponse(user);
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        // Always return success — do not reveal whether the email is registered
        if (userOpt.isEmpty()) {
            log.info("Password reset requested for unregistered email: {}", request.getEmail());
            return;
        }

        User user = userOpt.get();

        // Invalidate all existing unused tokens for this user
        passwordResetTokenRepository.invalidateAllByUserId(user.getId());

        // Generate a new secure token
        String rawToken = UUID.randomUUID().toString().replace("-", "");
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(rawToken)
                .expiresAt(LocalDateTime.now().plusMinutes(resetTokenExpiryMinutes))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = resetPasswordBaseUrl + "?token=" + rawToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        log.info("Password reset token generated for user id={}", user.getId());
    }

    @Override
    public VerifyTokenResponse verifyResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token.", HttpStatus.BAD_REQUEST));

        if (resetToken.isExpired()) {
            throw new BusinessException("Reset token has expired. Please request a new one.", HttpStatus.GONE);
        }
        if (resetToken.getUsed()) {
            throw new BusinessException("Reset token has already been used.", HttpStatus.BAD_REQUEST);
        }

        String maskedEmail = maskEmail(resetToken.getUser().getEmail());
        return VerifyTokenResponse.builder()
                .email(maskedEmail)
                .expiresAt(resetToken.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New password and confirm password do not match.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token.", HttpStatus.BAD_REQUEST));

        if (resetToken.isExpired()) {
            throw new BusinessException("Reset token has expired. Please request a new one.", HttpStatus.GONE);
        }
        if (resetToken.getUsed()) {
            throw new BusinessException("Reset token has already been used.", HttpStatus.BAD_REQUEST);
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(request.getNewPassword(), user.getHashPassword())) {
            throw new BusinessException("New password must be different from the current password.");
        }

        user.setHashPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully for user id={}", user.getId());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Masks an email for safe display, e.g. "hung@example.com" → "h***@example.com"
     */
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return "***" + email.substring(atIndex);
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
