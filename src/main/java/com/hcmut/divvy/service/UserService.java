package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface UserService {

    /**
     * Returns all registered users in the system (Admin use).
     *
     * @return list of all user profiles
     */
    List<UserResponse> findAll();

    /**
     * Returns a single user by their ID.
     *
     * @param model contains the user ID to look up
     * @return the user's profile; throws 404 if not found
     */
    UserResponse findById(GetUserByIdModel model);

    /**
     * Creates a new user account (Admin use; bypasses self-registration flow).
     *
     * @param model user details (username, email, password, role)
     * @return the newly created user profile
     */
    UserResponse create(CreateUserModel model);

    /**
     * Updates the current user's profile information (display name, phone, etc.).
     *
     * @param model updated profile fields and the caller's username
     * @return the updated user profile
     */
    UserResponse updateProfile(UpdateUserModel model);

    /**
     * Changes the current user's password after verifying the old password.
     *
     * @param model contains the current password, the new password, and the
     *              caller's username
     */
    void changePassword(ChangePasswordModel model);
}
