package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.repository.GroupInvitationRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InvitationValidator {

    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupValidator groupValidator;

    public User validateUserExists(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public User validateInviteeExists(Integer inviteeId) {
        return userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", inviteeId));
    }

    public GroupInvitation validateInvitationExists(Integer invitationId) {
        return groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", invitationId));
    }

    public void validateSendInvitation(Integer groupId, Integer inviterId, Integer inviteeId) {
        // Must be group OWNER
        groupValidator.validateIsAdmin(groupId, inviterId);

        // Invitee cannot already be a member
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, inviteeId)) {
            throw new BusinessException("User is already a member of this group.", HttpStatus.BAD_REQUEST);
        }

        // Check if there is already a PENDING invitation for this user
        if (groupInvitationRepository.existsByGroupIdAndInviteeIdAndStatus(groupId, inviteeId, InvitationStatus.PENDING)) {
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
            invitation.setStatus(InvitationStatus.EXPIRED);
            groupInvitationRepository.save(invitation);
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

    public void validateRevokeInvitation(GroupInvitation invitation, User caller) {
        // Must be group OWNER
        groupValidator.validateIsAdmin(invitation.getGroup().getId(), caller.getId());

        // Must be PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException("Only pending invitations can be revoked.", HttpStatus.BAD_REQUEST);
        }
    }
}
