package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.*;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "currentUsername", source = "currentUsername")
    CreateGroupModel toModel(CreateGroupRequest request, String currentUsername);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    UpdateGroupModel toModel(UpdateGroupRequest request, Integer groupId, String currentUsername);

    default FindMyGroupsModel toFindMyGroupsModel(String currentUsername, Pageable pageable) {
        return FindMyGroupsModel.builder()
                .currentUsername(currentUsername)
                .pageable(pageable)
                .build();
    }

    default GetGroupByIdModel toGetGroupByIdModel(Integer groupId, String currentUsername) {
        return GetGroupByIdModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .build();
    }

    default DeleteGroupModel toDeleteGroupModel(Integer groupId, String currentUsername) {
        return DeleteGroupModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "model.name")
    @Mapping(target = "note", source = "model.note")
    @Mapping(target = "startDate", source = "model.startDate")
    @Mapping(target = "endDate", source = "model.endDate")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdBy", source = "creator")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Group toEntity(CreateGroupModel model, User creator, Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "model.name")
    @Mapping(target = "note", source = "model.note")
    @Mapping(target = "startDate", source = "model.startDate")
    @Mapping(target = "endDate", source = "model.endDate")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateGroupModel model, Category category, @MappingTarget Group group);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdBy", source = "createdBy")
    GroupResponse toResponse(Group group);

    default GroupResponse.CategoryInfo toCategoryInfo(Category category) {
        if (category == null) return null;
        return GroupResponse.CategoryInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    default GroupResponse.UserInfo toUserInfo(User user) {
        if (user == null) return null;
        return GroupResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
