package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetValidator {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Finds and validates that a reset token exists, is unused, and has not expired.
     *
     * @param token the raw reset token string
     * @return the valid {@link PasswordResetToken} entity
     * @throws BusinessException if the token is invalid, already used, or expired
     */
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(
                        "Invalid or expired reset token.", HttpStatus.BAD_REQUEST));

        if (resetToken.isExpired()) {
            throw new BusinessException(
                    "Reset token has expired. Please request a new one.", HttpStatus.GONE);
        }
        if (resetToken.getUsed()) {
            throw new BusinessException(
                    "Reset token has already been used.", HttpStatus.BAD_REQUEST);
        }

        return resetToken;
    }

    /**
     * Validates all business rules for a reset password request:
     * <ul>
     *   <li>newPassword and confirmPassword must match</li>
     *   <li>the token must be valid (delegates to {@link #validateToken})</li>
     *   <li>new password must differ from the current hashed password</li>
     * </ul>
     *
     * @param request the reset password request DTO
     * @param user    the user whose password is being reset
     * @return the valid {@link PasswordResetToken} entity ready to be marked as used
     */
    public PasswordResetToken validateResetPasswordRequest(ResetPasswordRequest request, User user) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New password and confirm password do not match.");
        }

        PasswordResetToken resetToken = validateToken(request.getToken());

        if (passwordEncoder.matches(request.getNewPassword(), user.getHashPassword())) {
            throw new BusinessException("New password must be different from the current password.");
        }

        return resetToken;
    }
}
