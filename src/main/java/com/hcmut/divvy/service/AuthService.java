package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.service.model.*;

public interface AuthService {

    /**
     * Registers a new user account and issues a JWT token upon success.
     *
     * @param model registration payload (username, email, password, etc.)
     * @return JWT token and user profile in the response
     */
    AuthResponse register(RegisterModel model);

    /**
     * Authenticates a user with username and password and issues a JWT token.
     *
     * @param model login credentials (username + password)
     * @return JWT token and user profile in the response
     */
    AuthResponse login(LoginModel model);

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param username the username extracted from the JWT token
     * @return the user's profile data
     */
    UserResponse getCurrentUser(String username);

    /**
     * Sends a password-reset email containing a one-time reset link.
     *
     * @param model contains the user's registered email address
     */
    void forgotPassword(ForgotPasswordModel model);

    /**
     * Validates a password-reset token and returns its validity status.
     *
     * @param model contains the reset token string
     * @return token validity status and associated metadata
     */
    VerifyTokenResponse verifyResetToken(VerifyResetTokenModel model);

    /**
     * Resets the user's password using a valid one-time reset token.
     *
     * @param model contains the reset token and the new password
     */
    void resetPassword(ResetPasswordModel model);
}
