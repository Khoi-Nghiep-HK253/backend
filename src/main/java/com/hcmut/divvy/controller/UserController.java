package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.service.UserService;
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

    @GetMapping
    @Operation(summary = "Get list of all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer id) {
        UserResponse user = userService.findById(userMapper.toGetUserByIdModel(id));
        return ResponseEntity.ok(ApiResponse.ok(user, "User retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create a new user (Admin endpoint)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(userMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "User created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        UserResponse updated = userService.updateProfile(userMapper.toModel(request, id, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(updated, "User updated successfully"));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Integer id,
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        userService.changePassword(userMapper.toModel(request, id, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }
}
