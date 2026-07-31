package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.mapper.UserMapper;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.UserService;
import com.hcmut.divvy.service.model.*;
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
    public UserResponse findById(GetUserByIdModel model) {
        User user = userRepository.findById(model.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", model.getId()));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserModel model) {
        userValidator.validateCreateUser(model);
        User user = userMapper.toEntity(model);
        user.setHashPassword(passwordEncoder.encode(model.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateUserModel model) {
        User user = userRepository.findById(model.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", model.getId()));

        userValidator.validateOwnership(user, model.getCurrentUsername());

        userMapper.updatePartial(model, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordModel model) {
        User user = userRepository.findById(model.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", model.getId()));

        userValidator.validateOwnership(user, model.getCurrentUsername());

        userValidator.validateChangePassword(model, user);

        user.setHashPassword(passwordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);
    }
}
