package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Integer id);
    UserResponse create(CreateUserRequest request);

    /**
     * Updates a user's profile (firstname, lastname, phone, avatar).
     * Only the authenticated user can update their own profile.
     *
     * @param id              the user's ID
     * @param request         fields to update (null fields are ignored)
     * @param currentUsername the username extracted from the JWT token
     */
    UserResponse updateProfile(Integer id, UpdateUserRequest request, String currentUsername);

    /**
     * Changes a user's password after verifying their current password.
     * Only the authenticated user can change their own password.
     *
     * @param id              the user's ID
     * @param request         currentPassword + newPassword
     * @param currentUsername the username extracted from the JWT token
     */
    void changePassword(Integer id, ChangePasswordRequest request, String currentUsername);
}
