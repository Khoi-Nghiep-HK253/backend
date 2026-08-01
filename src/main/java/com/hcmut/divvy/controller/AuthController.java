package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.mapper.AuthMapper;
import com.hcmut.divvy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user registration, login, profile retrieval, and password recovery")
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        AuthResponse response = authService.register(authMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login to system and obtain JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(authMapper.toModel(request));
        return ResponseEntity.ok(ApiResponse.ok(response, "User logged in successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(response, "Current user retrieved successfully"));
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link via email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(authMapper.toModel(request));
        return ResponseEntity.ok(ApiResponse.ok(null,
                "If this email is registered, a password reset link has been sent."));
    }

    @GetMapping("/reset-password/verify")
    @Operation(summary = "Verify password reset token validity")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyResetToken(@RequestParam String token) {
        VerifyTokenResponse response = authService.verifyResetToken(authMapper.toVerifyTokenModel(token));
        return ResponseEntity.ok(ApiResponse.ok(response, "Token is valid"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(authMapper.toModel(request));
        return ResponseEntity.ok(ApiResponse.ok(null,
                "Password has been reset successfully. Please log in with your new password."));
    }
}
