package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.dto.response.VerifyTokenResponse;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    RegisterModel toModel(CreateUserRequest request);

    LoginModel toModel(LoginRequest request);

    ForgotPasswordModel toModel(ForgotPasswordRequest request);

    ResetPasswordModel toModel(ResetPasswordRequest request);

    default VerifyResetTokenModel toVerifyTokenModel(String token) {
        if (token == null)
            return null;
        return VerifyResetTokenModel.builder().token(token).build();
    }

    default AuthResponse toAuthResponse(String accessToken, UserResponse user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .user(user)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PasswordResetToken toPasswordResetToken(User user, String token, LocalDateTime expiresAt, boolean used);

    VerifyTokenResponse toVerifyTokenResponse(String email, LocalDateTime expiresAt);
}