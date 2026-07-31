package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.UserService;
import com.hcmut.divvy.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        userValidator.validateCreateUser(request);
        User user = userMapper.toEntity(request);
        user.setHashPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Integer id, UpdateUserRequest request, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userValidator.validateOwnership(user, currentUsername);

        userMapper.updatePartial(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordRequest request, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userValidator.validateOwnership(user, currentUsername);

        userValidator.validateChangePassword(request, user);

        user.setHashPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
