package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InvitationValidator {

    public void validateSendInvitation(GroupMember inviterMember, boolean isAlreadyMember, boolean hasPendingInvitation) {
        // Must be group OWNER
        if (inviterMember == null || GroupRole.OWNER != inviterMember.getRole()) {
            throw new BusinessException("Only group admins can perform this action.", HttpStatus.FORBIDDEN);
        }

        // Invitee cannot already be a member
        if (isAlreadyMember) {
            throw new BusinessException("User is already a member of this group.", HttpStatus.BAD_REQUEST);
        }

        // Check if there is already a PENDING invitation for this user
        if (hasPendingInvitation) {
            throw new BusinessException("A pending invitation already exists for this user.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateAcceptInvitation(GroupInvitation invitation, User caller) {
        // Must be the designated invitee
        if (!invitation.getInvitee().getId().equals(caller.getId())) {
            throw new BusinessException("You are not authorized to respond to this invitation.", HttpStatus.FORBIDDEN);
        }

        // Must be PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException("Invitation is no longer pending.", HttpStatus.BAD_REQUEST);
        }

        // Check expiration
        if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Invitation has expired.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateDeclineInvitation(GroupInvitation invitation, User caller) {
        // Must be the designated invitee
        if (!invitation.getInvitee().getId().equals(caller.getId())) {
            throw new BusinessException("You are not authorized to respond to this invitation.", HttpStatus.FORBIDDEN);
        }

        // Must be PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException("Invitation is no longer pending.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateRevokeInvitation(GroupInvitation invitation, GroupMember callerMember) {
        // Must be group OWNER
        if (callerMember == null || GroupRole.OWNER != callerMember.getRole()) {
            throw new BusinessException("Only group admins can perform this action.", HttpStatus.FORBIDDEN);
        }

        // Must be PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException("Only pending invitations can be revoked.", HttpStatus.BAD_REQUEST);
        }
    }
}
