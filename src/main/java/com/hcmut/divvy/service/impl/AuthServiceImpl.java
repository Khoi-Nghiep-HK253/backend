package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.helper.StringHelper;
import com.hcmut.divvy.helper.TokenHelper;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.repository.PasswordResetTokenRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.security.JwtTokenProvider;
import com.hcmut.divvy.service.AuthService;
import com.hcmut.divvy.service.EmailService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.PasswordResetValidator;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordResetValidator passwordResetValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.reset-password.expiry-minutes:30}")
    private int resetTokenExpiryMinutes;

    @Value("${app.reset-password.base-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    @Override
    @Transactional
    public AuthResponse register(RegisterModel model) {
        userValidator.validateCreateUser(model.getUsername(), model.getEmail());

        User user = userMapper.toEntity(model);
        user.setHashPassword(passwordEncoder.encode(model.getPassword()));

        User saved = userRepository.save(user);
        String token = tokenProvider.generateToken(saved.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userMapper.toResponse(saved))
                .build();
    }

    @Override
    public AuthResponse login(LoginModel model) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(model.getUsernameOrEmail(), model.getPassword())
        );

        User user = userRepository.findByUsername(model.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(model.getUsernameOrEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", model.getUsernameOrEmail())));

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

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordModel model) {
        Optional<User> userOpt = userRepository.findByEmail(model.getEmail());

        if (userOpt.isEmpty()) {
            log.info("Password reset requested for unregistered email: {}", model.getEmail());
            return;
        }

        User user = userOpt.get();

        passwordResetTokenRepository.invalidateAllByUserId(user.getId());

        String rawToken = TokenHelper.generateToken();
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
    public VerifyTokenResponse verifyResetToken(VerifyResetTokenModel model) {
        PasswordResetToken resetToken = passwordResetValidator.validateToken(model.getToken());

        String maskedEmail = StringHelper.maskEmail(resetToken.getUser().getEmail());
        return VerifyTokenResponse.builder()
                .email(maskedEmail)
                .expiresAt(resetToken.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordModel model) {
        PasswordResetToken preCheck = passwordResetTokenRepository.findByToken(model.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token.", HttpStatus.BAD_REQUEST));

        PasswordResetToken resetToken = passwordResetValidator.validateResetPasswordRequest(model.getToken(), model.getNewPassword(), model.getConfirmPassword(), preCheck.getUser());

        User user = resetToken.getUser();
        user.setHashPassword(passwordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully for user id={}", user.getId());
    }
}
