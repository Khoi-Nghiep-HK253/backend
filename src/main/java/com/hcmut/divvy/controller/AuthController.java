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
import com.hcmut.divvy.service.model.*;
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

    /**
     * Register a new user account.
     * <p>
     * Validates that username and email are not already taken, encodes the password
     * with BCrypt, saves the User with the default role {@code USER}, then returns
     * a JWT token together with the user profile.
     *
     * @param request registration payload (username, email, password, firstname, lastname, phone)
     * @return {@code 201 Created} with AuthResponse (accessToken, tokenType, user)
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        RegisterModel model = authMapper.toModel(request);
        AuthResponse response = authService.register(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    /**
     * Authenticate and obtain a JWT token.
     * <p>
     * Delegates credential verification to Spring Security's {@code AuthenticationManager}.
     * The {@code usernameOrEmail} field accepts both a username and an email address.
     *
     * @param request login payload (usernameOrEmail, password)
     * @return {@code 200 OK} with AuthResponse (accessToken, tokenType, user)
     */
    @PostMapping("/login")
    @Operation(summary = "Login to system and obtain JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginModel model = authMapper.toModel(request);
        AuthResponse response = authService.login(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "User logged in successfully"));
    }

    /**
     * Retrieve the profile of the currently authenticated user.
     * <p>
     * The username is extracted from the JWT stored in {@code SecurityContextHolder}.
     *
     * @param authentication Spring Security authentication context
     * @return {@code 200 OK} with the current user's UserResponse
     */
    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(response, "Current user retrieved successfully"));
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    /**
     * Request a password-reset link to be sent via email.
     * <p>
     * Looks up the user by email. If not found, returns {@code 200 OK} without
     * disclosing whether the email is registered (prevents user-enumeration attacks).
     * If found, invalidates all previous reset tokens, generates a new time-limited token,
     * and sends an email containing the reset link.
     *
     * @param request payload containing the email address
     * @return {@code 200 OK} (always, regardless of whether the email exists)
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link via email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordModel model = authMapper.toModel(request);
        authService.forgotPassword(model);
        return ResponseEntity.ok(ApiResponse.ok(null,
                "If this email is registered, a password reset link has been sent."));
    }

    /**
     * Verify the validity of a password-reset token.
     * <p>
     * Checks that the token exists, has not been used ({@code used=false}), and has not expired.
     * Returns a masked email address and the token's expiry time.
     *
     * @param token the reset token received from the email link
     * @return {@code 200 OK} with VerifyTokenResponse (maskedEmail, expiresAt)
     */
    @GetMapping("/reset-password/verify")
    @Operation(summary = "Verify password reset token validity")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyResetToken(@RequestParam String token) {
        VerifyResetTokenModel model = authMapper.toVerifyTokenModel(token);
        VerifyTokenResponse response = authService.verifyResetToken(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Token is valid"));
    }

    /**
     * Reset the user's password using a valid reset token.
     * <p>
     * Fully validates the token (exists, unused, not expired) and that
     * {@code newPassword} matches {@code confirmPassword}.
     * On success, the token is marked {@code used=true} to prevent reuse.
     *
     * @param request payload containing token, newPassword, and confirmPassword
     * @return {@code 200 OK}
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordModel model = authMapper.toModel(request);
        authService.resetPassword(model);
        return ResponseEntity.ok(ApiResponse.ok(null,
                "Password has been reset successfully. Please log in with your new password."));
    }
}
