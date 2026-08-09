package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.AddMemberRequest;
import com.hcmut.divvy.dto.request.UpdateMemberRoleRequest;
import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMemberMapper {

    default GetMembersModel toGetMembersModel(Integer groupId, String currentUsername) {
        return GetMembersModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    AddMemberModel toModel(AddMemberRequest request, Integer groupId, String currentUsername);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "memberId", source = "memberId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    UpdateMemberRoleModel toModel(UpdateMemberRoleRequest request, Integer groupId, Integer memberId,
            String currentUsername);

    default RemoveMemberModel toRemoveMemberModel(Integer groupId, Integer memberId, String currentUsername) {
        return RemoveMemberModel.builder()
                .groupId(groupId)
                .memberId(memberId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "joinedAt", source = "createdAt")
    @Mapping(target = "user", source = "user")
    GroupMemberResponse toResponse(GroupMember member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "group")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GroupMember toEntity(Group group, User user, GroupRole role);

    default GroupMemberResponse.UserInfo toUserInfo(User user) {
        if (user == null)
            return null;
        return GroupMemberResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
