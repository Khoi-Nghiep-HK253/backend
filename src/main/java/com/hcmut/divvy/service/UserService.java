package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(GetUserByIdModel model);
    UserResponse create(CreateUserModel model);
    UserResponse updateProfile(UpdateUserModel model);
    void changePassword(ChangePasswordModel model);
}
