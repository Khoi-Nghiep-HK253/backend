package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;

public interface AuthService {
    AuthResponse register(CreateUserRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String username);

    /**
     * Initiates the password reset flow by generating a one-time token and
     * sending a reset link to the user's email. Always returns success to
     * prevent user enumeration attacks.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Verifies that a reset token exists, is unused, and has not expired.
     *
     * @return masked email and expiry time of the token
     */
    VerifyTokenResponse verifyResetToken(String token);

    /**
     * Resets the user's password using a valid one-time reset token.
     * Marks the token as used after a successful reset.
     */
    void resetPassword(ResetPasswordRequest request);
}
