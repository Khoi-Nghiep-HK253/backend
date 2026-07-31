package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.service.model.*;

public interface AuthService {
    AuthResponse register(RegisterModel model);

    AuthResponse login(LoginModel model);

    UserResponse getCurrentUser(String username);

    void forgotPassword(ForgotPasswordModel model);

    VerifyTokenResponse verifyResetToken(VerifyResetTokenModel model);

    void resetPassword(ResetPasswordModel model);
}
