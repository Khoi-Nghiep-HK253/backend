package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.ForgotPasswordRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.request.ResetPasswordRequest;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;

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
}
