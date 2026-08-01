package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.service.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateCreateUser(boolean usernameExists, boolean emailExists) {
        if (usernameExists) {
            throw new BusinessException("Username already exists", HttpStatus.CONFLICT);
        }
        if (emailExists) {
            throw new BusinessException("Email already exists", HttpStatus.CONFLICT);
        }
    }

    public void validateChangePassword(ChangePasswordRequest request, User user, PasswordEncoder passwordEncoder) {
        validateChangePassword(request.getCurrentPassword(), request.getNewPassword(), user, passwordEncoder);
    }

    public void validateChangePassword(ChangePasswordModel model, User user, PasswordEncoder passwordEncoder) {
        validateChangePassword(model.getOldPassword(), model.getNewPassword(), user, passwordEncoder);
    }

    public void validateChangePassword(String oldPassword, String newPassword, User user, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(oldPassword, user.getHashPassword())) {
            throw new BusinessException("Current password is incorrect.", HttpStatus.BAD_REQUEST);
        }
        if (newPassword.equals(oldPassword)) {
            throw new BusinessException("New password must be different from the current password.");
        }
    }

    /**
     * Ensures the currently authenticated user is the owner of the given user
     * account.
     *
     * @param user            the target user entity
     * @param currentUsername the username extracted from the JWT token
     * @throws BusinessException 403 if the caller is not the owner
     */
    public void validateOwnership(User user, String currentUsername) {
        if (!user.getUsername().equals(currentUsername)) {
            throw new BusinessException("You are not authorized to modify this user's data.", HttpStatus.FORBIDDEN);
        }
    }
}
