package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.ChangePasswordRequest;
import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.UserRole;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = UserRole.class)
public interface UserMapper {

    default GetUserByIdModel toGetUserByIdModel(Integer id) {
        return GetUserByIdModel.builder().id(id).build();
    }

    CreateUserModel toModel(CreateUserRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "currentUsername", source = "currentUsername")
    UpdateUserModel toModel(UpdateUserRequest request, Integer id, String currentUsername);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "currentUsername", source = "currentUsername")
    ChangePasswordModel toModel(ChangePasswordRequest request, Integer id, String currentUsername);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashPassword", source = "password")
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashPassword", source = "password")
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashPassword", source = "password")
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterModel model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "hashPassword", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePartial(UpdateUserModel model, @MappingTarget User user);
}
