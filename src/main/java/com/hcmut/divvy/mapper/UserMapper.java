package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateUserRequest;
import com.hcmut.divvy.dto.request.UpdateUserRequest;
import com.hcmut.divvy.dto.response.UserResponse;
import com.hcmut.divvy.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashPassword", source = "password")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Partially updates an existing User entity from an UpdateUserRequest.
     * Fields that are {@code null} in the request are left unchanged on the target.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "hashPassword", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePartial(UpdateUserRequest request, @MappingTarget User user);
}
