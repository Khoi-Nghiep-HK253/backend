package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.service.UserService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for listing users, viewing profile, updating user info, and changing password")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Retrieve a list of all users in the system.
     * <p>
     * No pagination — returns the full list.
     *
     * @return {@code 200 OK} with a list of UserResponse
     */
    @GetMapping
    @Operation(summary = "Get list of all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(users, "Users retrieved successfully"));
    }

    /**
     * Retrieve the details of a specific user by their ID.
     *
     * @param id the user's ID
     * @return {@code 200 OK} with UserResponse; {@code 404} if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer id) {
        GetUserByIdModel model = userMapper.toGetUserByIdModel(id);
        UserResponse user = userService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(user, "User retrieved successfully"));
    }

    /**
     * Create a new user account (admin endpoint).
     * <p>
     * Validates that the username and email are not already taken before
     * persisting.
     * The password is encoded with BCrypt.
     *
     * @param request the new user's information
     * @return {@code 201 Created} with UserResponse; {@code 409} if username or
     *         email already exists
     */
    @PostMapping
    @Operation(summary = "Create a new user (Admin endpoint)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserModel model = userMapper.toModel(request);
        UserResponse created = userService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "User created successfully"));
    }

    /**
     * Update the profile information of a user.
     * <p>
     * Only the account owner is permitted to update their own profile.
     * Fields that are {@code null} in the request are left unchanged (partial
     * update).
     *
     * @param id             the ID of the user to update
     * @param request        the fields to update (firstname, lastname, phone)
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with the updated UserResponse;
     *         {@code 404} if not found; {@code 403} if the caller is not the owner
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        UpdateUserModel model = userMapper.toModel(request, id, authentication.getName());
        UserResponse updated = userService.updateProfile(model);
        return ResponseEntity.ok(ApiResponse.ok(updated, "User updated successfully"));
    }

    /**
     * Change the password of the authenticated user.
     * <p>
     * Verifies that {@code oldPassword} matches the current hash and that
     * {@code newPassword} differs from the old one.
     * Only the account owner may perform this action.
     *
     * @param id             the ID of the user whose password should be changed
     * @param request        the password change payload (oldPassword, newPassword)
     * @param authentication the currently authenticated user
     * @return {@code 200 OK};
     *         {@code 400} if the old password is wrong or the new password is the
     *         same;
     *         {@code 403} if the caller is not the owner
     */
    @PatchMapping("/{id}/password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Integer id,
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        ChangePasswordModel model = userMapper.toModel(request, id, authentication.getName());
        userService.changePassword(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }
}
