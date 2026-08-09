package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.GroupRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberValidator {

    /**
     * Validates that the given user is a member of the specified group.
     */
    public void validateIsMember(GroupMember member) {
        if (member == null) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Validates that the given user is an OWNER of the specified group.
     */
    public void validateIsAdmin(GroupMember member) {
        validateIsMember(member);
        if (GroupRole.OWNER != member.getRole()) {
            throw new BusinessException("Only group owners can perform this action.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Validates that a user can be added to a group (i.e. not already a member).
     */
    public void validateAddMember(boolean isAlreadyMember) {
        if (isAlreadyMember) {
            throw new BusinessException("User is already a member of this group.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates member role update rules (e.g., cannot downgrade the last OWNER).
     */
    public void validateUpdateRole(GroupMember member, String newRoleStr, long ownerCount) {
        GroupRole newRole = GroupRole.valueOf(newRoleStr);
        if (GroupRole.OWNER == member.getRole() && GroupRole.MEMBER == newRole) {
            if (ownerCount <= 1) {
                throw new BusinessException("Cannot downgrade the last owner of the group.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Validates member removal rules (authorization check & last OWNER protection).
     */
    public void validateRemoveMember(GroupMember targetMember, Integer callerUserId, boolean isCallerAdmin,
            long ownerCount) {
        boolean isSelf = targetMember.getUser().getId().equals(callerUserId);
        if (!isSelf && !isCallerAdmin) {
            throw new BusinessException("You are not authorized to remove this member.", HttpStatus.FORBIDDEN);
        }

        if (GroupRole.OWNER == targetMember.getRole()) {
            if (ownerCount <= 1) {
                throw new BusinessException("Cannot remove the last owner of the group.", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
