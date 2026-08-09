package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.helper.StringHelper;
import com.hcmut.divvy.dto.response.GroupPreviewResponse;
import com.hcmut.divvy.dto.response.ShareLinkResponse;
import com.hcmut.divvy.entity.Activity;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.GroupShareLink;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.ShareLinkStatus;
import com.hcmut.divvy.mapper.GroupShareLinkMapper;
import com.hcmut.divvy.repository.ActivityRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import com.hcmut.divvy.repository.GroupShareLinkRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.GroupShareLinkService;
import com.hcmut.divvy.service.model.CreateShareLinkModel;
import com.hcmut.divvy.service.model.GetGroupPreviewModel;
import com.hcmut.divvy.service.model.GetGroupShareLinksModel;
import com.hcmut.divvy.service.model.JoinViaLinkModel;
import com.hcmut.divvy.service.model.RevokeShareLinkModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupShareLinkServiceImpl implements GroupShareLinkService {

    private final GroupShareLinkRepository shareLinkRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ActivityRepository activityRepository;
    private final GroupShareLinkMapper shareLinkMapper;

    @Override
    @Transactional
    public ShareLinkResponse createShareLink(CreateShareLinkModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        Group group = findGroupById(model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));

        if (callerMember.getRole() != GroupRole.OWNER) {
            throw new BusinessException("Only group Owner can generate share links.", HttpStatus.FORBIDDEN);
        }

        String inviteCode = "g_" + StringHelper.generateRandomAlphanumeric(16);
        LocalDateTime expiresAt = (model.getExpireHours() != null && model.getExpireHours() > 0)
                ? LocalDateTime.now().plusHours(model.getExpireHours())
                : null;

        GroupShareLink shareLink = GroupShareLink.builder()
                .group(group)
                .createdBy(caller)
                .inviteCode(inviteCode)
                .maxUses(model.getMaxUses() != null && model.getMaxUses() > 0 ? model.getMaxUses() : null)
                .usedCount(0)
                .expiresAt(expiresAt)
                .status(ShareLinkStatus.ACTIVE)
                .build();

        GroupShareLink saved = shareLinkRepository.save(shareLink);
        return shareLinkMapper.toShareLinkResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareLinkResponse> getGroupShareLinks(GetGroupShareLinksModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        groupMemberRepository.findByGroupIdAndUserId(model.getGroupId(), caller.getId())
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));

        List<GroupShareLink> links = shareLinkRepository.findByGroupId(model.getGroupId());
        return links.stream().map(shareLinkMapper::toShareLinkResponse).toList();
    }

    @Override
    @Transactional
    public ShareLinkResponse revokeShareLink(RevokeShareLinkModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        GroupShareLink shareLink = shareLinkRepository.findById(model.getLinkId())
                .orElseThrow(() -> new BusinessException("Share link not found.", HttpStatus.NOT_FOUND));

        if (!shareLink.getGroup().getId().equals(model.getGroupId())) {
            throw new BusinessException("Share link does not belong to specified group.", HttpStatus.BAD_REQUEST);
        }

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(shareLink.getGroup().getId(), caller.getId())
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));

        if (callerMember.getRole() != GroupRole.OWNER) {
            throw new BusinessException("Only group Owner can revoke share links.", HttpStatus.FORBIDDEN);
        }

        shareLink.setStatus(ShareLinkStatus.REVOKED);
        GroupShareLink saved = shareLinkRepository.save(shareLink);
        return shareLinkMapper.toShareLinkResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupPreviewResponse getGroupPreview(GetGroupPreviewModel model) {
        GroupShareLink shareLink = shareLinkRepository.findByInviteCode(model.getInviteCode()).orElse(null);

        if (shareLink == null) {
            return shareLinkMapper.toGroupPreviewResponse(null, 0, model.getInviteCode(), false, "Invalid or non-existent invite code.");
        }

        if (shareLink.getStatus() != ShareLinkStatus.ACTIVE) {
            return shareLinkMapper.toGroupPreviewResponse(shareLink.getGroup(), 0, model.getInviteCode(), false, "This invite link has been revoked.");
        }

        if (shareLink.getExpiresAt() != null && shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            return shareLinkMapper.toGroupPreviewResponse(shareLink.getGroup(), 0, model.getInviteCode(), false, "This invite link has expired.");
        }

        if (shareLink.getMaxUses() != null && shareLink.getUsedCount() >= shareLink.getMaxUses()) {
            return shareLinkMapper.toGroupPreviewResponse(shareLink.getGroup(), 0, model.getInviteCode(), false, "This invite link has reached its maximum usage limit.");
        }

        Group group = shareLink.getGroup();
        int memberCount = (int) groupMemberRepository.countByGroupId(group.getId());
        return shareLinkMapper.toGroupPreviewResponse(group, memberCount, model.getInviteCode(), true, null);
    }

    @Override
    @Transactional
    public ShareLinkResponse joinGroupViaLink(JoinViaLinkModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());

        GroupShareLink shareLink = shareLinkRepository.findByInviteCode(model.getInviteCode())
                .orElseThrow(() -> new BusinessException("Invalid invite code.", HttpStatus.NOT_FOUND));

        if (shareLink.getStatus() != ShareLinkStatus.ACTIVE) {
            throw new BusinessException("This invite link is no longer active.", HttpStatus.BAD_REQUEST);
        }

        if (shareLink.getExpiresAt() != null && shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            shareLink.setStatus(ShareLinkStatus.EXPIRED);
            shareLinkRepository.save(shareLink);
            throw new BusinessException("This invite link has expired.", HttpStatus.BAD_REQUEST);
        }

        if (shareLink.getMaxUses() != null && shareLink.getUsedCount() >= shareLink.getMaxUses()) {
            throw new BusinessException("This invite link has reached its maximum usage limit.", HttpStatus.BAD_REQUEST);
        }

        Group group = shareLink.getGroup();

        boolean alreadyMember = groupMemberRepository.existsByGroupIdAndUserId(group.getId(), caller.getId());
        if (alreadyMember) {
            throw new BusinessException("You are already a member of this group.", HttpStatus.BAD_REQUEST);
        }

        // Save member
        GroupMember member = GroupMember.builder()
                .group(group)
                .user(caller)
                .role(GroupRole.MEMBER)
                .build();
        groupMemberRepository.save(member);

        // Update share link usage count
        shareLink.setUsedCount(shareLink.getUsedCount() + 1);
        if (shareLink.getMaxUses() != null && shareLink.getUsedCount() >= shareLink.getMaxUses()) {
            shareLink.setStatus(ShareLinkStatus.EXPIRED);
        }
        GroupShareLink updatedLink = shareLinkRepository.save(shareLink);

        // Activity log
        activityRepository.save(Activity.builder()
                .user(caller)
                .entityType("GROUP")
                .entityId(group.getId())
                .topic("Gia nhập nhóm")
                .description(caller.getUsername() + " đã gia nhập nhóm '" + group.getName() + "' qua liên kết chia sẻ")
                .build());

        return shareLinkMapper.toShareLinkResponse(updatedLink);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username, HttpStatus.NOT_FOUND));
    }

    private Group findGroupById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Group not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}
