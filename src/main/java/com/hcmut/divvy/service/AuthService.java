package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.LoginRequest;
import com.hcmut.divvy.dto.response.AuthResponse;
import com.hcmut.divvy.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(CreateUserRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String username);
}
