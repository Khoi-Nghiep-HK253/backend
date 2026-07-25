package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Integer id);
    UserResponse create(CreateUserRequest request);
}
