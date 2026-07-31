package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateCreateUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", HttpStatus.CONFLICT);
        }
    }

    /**
     * Validates that the provided current password matches the user's stored password.
     *
     * @throws BusinessException 400 if currentPassword does not match
     */
    public void validateChangePassword(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getHashPassword())) {
            throw new BusinessException("Current password is incorrect.", HttpStatus.BAD_REQUEST);
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BusinessException("New password must be different from the current password.");
        }
    }

    /**
     * Ensures the currently authenticated user is the owner of the given user account.
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
