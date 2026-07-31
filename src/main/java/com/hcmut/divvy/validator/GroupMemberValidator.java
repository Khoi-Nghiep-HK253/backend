package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupMemberValidator {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * Validates that the group exists in the system.
     */
    public void validateGroupExists(Integer groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", "id", groupId);
        }
    }

    /**
     * Validates that the given user is a member of the specified group.
     */
    public void validateIsMember(Integer groupId, Integer userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Validates that the given user is an OWNER of the specified group.
     */
    public void validateIsAdmin(Integer groupId, Integer userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));
        if (GroupRole.OWNER != member.getRole()) {
            throw new BusinessException("Only group owners can perform this action.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Validates that a user can be added to a group (i.e. not already a member).
     */
    public void validateAddMember(Integer groupId, Integer targetUserId) {
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, targetUserId)) {
            throw new BusinessException("User is already a member of this group.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates member role update rules (e.g., cannot downgrade the last OWNER).
     */
    public void validateUpdateRole(GroupMember member, String newRoleStr) {
        GroupRole newRole = GroupRole.valueOf(newRoleStr);
        if (GroupRole.OWNER == member.getRole() && GroupRole.MEMBER == newRole) {
            long ownerCount = groupMemberRepository.countByGroupIdAndRole(member.getGroup().getId(), GroupRole.OWNER);
            if (ownerCount <= 1) {
                throw new BusinessException("Cannot downgrade the last owner of the group.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Validates member removal rules (authorization check & last OWNER protection).
     */
    public void validateRemoveMember(GroupMember targetMember, Integer callerUserId, boolean isCallerAdmin) {
        boolean isSelf = targetMember.getUser().getId().equals(callerUserId);
        if (!isSelf && !isCallerAdmin) {
            throw new BusinessException("You are not authorized to remove this member.", HttpStatus.FORBIDDEN);
        }

        if (GroupRole.OWNER == targetMember.getRole()) {
            long ownerCount = groupMemberRepository.countByGroupIdAndRole(targetMember.getGroup().getId(),
                    GroupRole.OWNER);
            if (ownerCount <= 1) {
                throw new BusinessException("Cannot remove the last owner of the group.", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
