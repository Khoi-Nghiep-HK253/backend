package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.SendInvitationRequest;
import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    SendInvitationModel toModel(SendInvitationRequest request, Integer groupId, String currentUsername);

    default GetGroupInvitationsModel toGetGroupInvitationsModel(Integer groupId, InvitationStatus status, String currentUsername) {
        return GetGroupInvitationsModel.builder()
                .groupId(groupId)
                .status(status)
                .currentUsername(currentUsername)
                .build();
    }

    default GetMyInvitationsModel toGetMyInvitationsModel(InvitationStatus status, String currentUsername) {
        return GetMyInvitationsModel.builder()
                .status(status)
                .currentUsername(currentUsername)
                .build();
    }

    default AcceptInvitationModel toAcceptInvitationModel(Integer invitationId, String currentUsername) {
        return AcceptInvitationModel.builder()
                .invitationId(invitationId)
                .currentUsername(currentUsername)
                .build();
    }

    default DeclineInvitationModel toDeclineInvitationModel(Integer invitationId, String currentUsername) {
        return DeclineInvitationModel.builder()
                .invitationId(invitationId)
                .currentUsername(currentUsername)
                .build();
    }

    default RevokeInvitationModel toRevokeInvitationModel(Integer invitationId, String currentUsername) {
        return RevokeInvitationModel.builder()
                .invitationId(invitationId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "group")
    @Mapping(target = "inviter", source = "inviter")
    @Mapping(target = "invitee", source = "invitee")
    @Mapping(target = "status", expression = "java(com.hcmut.divvy.entity.enums.InvitationStatus.PENDING)")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "message", source = "model.message")
    @Mapping(target = "expiresAt", source = "model.expiresAt")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GroupInvitation toEntity(SendInvitationModel model, Group group, User inviter, User invitee, String token);

    @Mapping(target = "group", source = "group")
    @Mapping(target = "inviter", source = "inviter")
    @Mapping(target = "invitee", source = "invitee")
    InvitationResponse toResponse(GroupInvitation invitation);

    default InvitationResponse.GroupInfo toGroupInfo(Group group) {
        if (group == null) return null;
        return InvitationResponse.GroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }

    default InvitationResponse.UserInfo toUserInfo(User user) {
        if (user == null) return null;
        return InvitationResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }

    default AcceptInvitationResponse toAcceptResponse(GroupInvitation invitation) {
        if (invitation == null) return null;
        return AcceptInvitationResponse.builder()
                .invitationId(invitation.getId())
                .status(invitation.getStatus())
                .joinedGroup(AcceptInvitationResponse.JoinedGroupInfo.builder()
                        .id(invitation.getGroup().getId())
                        .name(invitation.getGroup().getName())
                        .build())
                .build();
    }

    default InvitationStatusResponse toStatusResponse(GroupInvitation invitation) {
        if (invitation == null) return null;
        return InvitationStatusResponse.builder()
                .invitationId(invitation.getId())
                .status(invitation.getStatus())
                .build();
    }
}
