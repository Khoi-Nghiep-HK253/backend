package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.facade.AuthFacade;
import com.hcmut.divvy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        AuthResponse response = authFacade.execute(AuthService.class, service -> service.register(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authFacade.execute(AuthService.class, service -> service.login(request));
        return ResponseEntity.ok(ApiResponse.ok(response, "User logged in successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse response = authFacade.execute(AuthService.class, service -> service.getCurrentUser(authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(response, "Current user retrieved successfully"));
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authFacade.executeVoid(AuthService.class, service -> service.forgotPassword(request));
        return ResponseEntity.ok(ApiResponse.ok(null,
                "If this email is registered, a password reset link has been sent."));
    }

    @GetMapping("/reset-password/verify")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyResetToken(@RequestParam String token) {
        VerifyTokenResponse response = authFacade.execute(AuthService.class, service -> service.verifyResetToken(token));
        return ResponseEntity.ok(ApiResponse.ok(response, "Token is valid"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authFacade.executeVoid(AuthService.class, service -> service.resetPassword(request));
        return ResponseEntity.ok(ApiResponse.ok(null,
                "Password has been reset successfully. Please log in with your new password."));
    }
}
