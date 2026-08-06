package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateShareLinkRequest;
import com.hcmut.divvy.dto.response.GroupPreviewResponse;
import com.hcmut.divvy.dto.response.ShareLinkResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupShareLink;
import com.hcmut.divvy.service.model.CreateShareLinkModel;
import com.hcmut.divvy.service.model.RevokeShareLinkModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupShareLinkMapper {

    default CreateShareLinkModel toCreateShareLinkModel(CreateShareLinkRequest request, Integer groupId, String currentUsername) {
        return CreateShareLinkModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .expireHours(request != null ? request.getExpireHours() : null)
                .maxUses(request != null ? request.getMaxUses() : null)
                .build();
    }

    default RevokeShareLinkModel toRevokeShareLinkModel(Integer groupId, Integer linkId, String currentUsername) {
        return RevokeShareLinkModel.builder()
                .groupId(groupId)
                .linkId(linkId)
                .currentUsername(currentUsername)
                .build();
    }

    default ShareLinkResponse toShareLinkResponse(GroupShareLink shareLink) {
        if (shareLink == null) return null;
        return ShareLinkResponse.builder()
                .id(shareLink.getId())
                .groupId(shareLink.getGroup() != null ? shareLink.getGroup().getId() : null)
                .groupName(shareLink.getGroup() != null ? shareLink.getGroup().getName() : null)
                .inviteCode(shareLink.getInviteCode())
                .createdByUsername(shareLink.getCreatedBy() != null ? shareLink.getCreatedBy().getUsername() : null)
                .maxUses(shareLink.getMaxUses())
                .usedCount(shareLink.getUsedCount())
                .expiresAt(shareLink.getExpiresAt())
                .status(shareLink.getStatus())
                .createdAt(shareLink.getCreatedAt())
                .build();
    }

    default GroupPreviewResponse toGroupPreviewResponse(Group group, Integer memberCount, String inviteCode, boolean isValid, String invalidReason) {
        if (group == null) {
            return GroupPreviewResponse.builder()
                    .inviteCode(inviteCode)
                    .isValid(false)
                    .invalidReason(invalidReason != null ? invalidReason : "Group not found")
                    .build();
        }
        return GroupPreviewResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .categoryName(group.getCategory() != null ? group.getCategory().getName() : null)
                .categoryIcon(group.getCategory() != null ? group.getCategory().getIcon() : null)
                .note(group.getNote())
                .createdByUsername(group.getCreatedBy() != null ? group.getCreatedBy().getUsername() : null)
                .memberCount(memberCount)
                .inviteCode(inviteCode)
                .isValid(isValid)
                .invalidReason(invalidReason)
                .build();
    }
}
