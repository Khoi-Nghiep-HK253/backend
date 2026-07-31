package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.facade.UserFacade;
import com.hcmut.divvy.service.UserService;
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
public class UserController {

    private final UserFacade userFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userFacade.execute(UserService.class, service -> service.findAll());
        return ResponseEntity.ok(ApiResponse.ok(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Integer id) {
        UserResponse user = userFacade.execute(UserService.class, service -> service.findById(id));
        return ResponseEntity.ok(ApiResponse.ok(user, "User retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userFacade.execute(UserService.class, service -> service.create(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        UserResponse updated = userFacade.execute(UserService.class,
                service -> service.updateProfile(id, request, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(updated, "User updated successfully"));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Integer id,
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        userFacade.executeVoid(UserService.class,
                service -> service.changePassword(id, request, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }
}
