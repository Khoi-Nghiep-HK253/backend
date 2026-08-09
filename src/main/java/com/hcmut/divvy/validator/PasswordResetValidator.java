package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.service.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetValidator {

    public void validateToken(PasswordResetToken resetToken) {
        if (resetToken == null) {
            throw new BusinessException(
                    "Invalid or expired reset token.", HttpStatus.BAD_REQUEST);
        }
        if (resetToken.isExpired()) {
            throw new BusinessException(
                    "Reset token has expired. Please request a new one.", HttpStatus.GONE);
        }
        if (Boolean.TRUE.equals(resetToken.getUsed())) {
            throw new BusinessException(
                    "Reset token has already been used.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateResetPasswordRequest(ResetPasswordModel model, PasswordResetToken resetToken, User user,
            PasswordEncoder passwordEncoder) {
        validateResetPasswordRequest(model.getToken(), model.getNewPassword(), model.getConfirmPassword(), resetToken,
                user, passwordEncoder);
    }

    public void validateResetPasswordRequest(String token, String newPassword, String confirmPassword,
            PasswordResetToken resetToken, User user, PasswordEncoder passwordEncoder) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("New password and confirm password do not match.");
        }

        validateToken(resetToken);

        if (passwordEncoder.matches(newPassword, user.getHashPassword())) {
            throw new BusinessException("New password must be different from the current password.");
        }
    }
}
