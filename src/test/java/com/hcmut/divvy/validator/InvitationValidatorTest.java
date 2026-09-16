package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationValidatorTest {

    private final InvitationValidator validator = new InvitationValidator();
    private final User invitee = User.builder().id(1).build();

    @Test
    void validateSendInvitation_throwsWhenInviterNotOwner() {
        GroupMember inviter = GroupMember.builder().role(GroupRole.MEMBER).build();
        assertThatThrownBy(() -> validator.validateSendInvitation(inviter, false, false))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateSendInvitation_throwsWhenInviterNull() {
        assertThatThrownBy(() -> validator.validateSendInvitation(null, false, false))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateSendInvitation_throwsWhenAlreadyMember() {
        GroupMember inviter = GroupMember.builder().role(GroupRole.OWNER).build();
        assertThatThrownBy(() -> validator.validateSendInvitation(inviter, true, false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User is already a member of this group.");
    }

    @Test
    void validateSendInvitation_throwsWhenPendingInvitationExists() {
        GroupMember inviter = GroupMember.builder().role(GroupRole.OWNER).build();
        assertThatThrownBy(() -> validator.validateSendInvitation(inviter, false, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A pending invitation already exists for this user.");
    }

    @Test
    void validateSendInvitation_passesWhenOwnerAndNoConflicts() {
        GroupMember inviter = GroupMember.builder().role(GroupRole.OWNER).build();
        validator.validateSendInvitation(inviter, false, false);
    }

    @Test
    void validateAcceptInvitation_throwsWhenNotInvitee() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(User.builder().id(2).build())
                .status(InvitationStatus.PENDING)
                .build();
        assertThatThrownBy(() -> validator.validateAcceptInvitation(invitation, invitee))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateAcceptInvitation_throwsWhenNotPending() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.DECLINED)
                .build();
        assertThatThrownBy(() -> validator.validateAcceptInvitation(invitation, invitee))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invitation is no longer pending.");
    }

    @Test
    void validateAcceptInvitation_throwsWhenExpired() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.PENDING)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
        assertThatThrownBy(() -> validator.validateAcceptInvitation(invitation, invitee))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invitation has expired.");
    }

    @Test
    void validateAcceptInvitation_passesWhenPendingAndNotExpired() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        validator.validateAcceptInvitation(invitation, invitee);
    }

    @Test
    void validateAcceptInvitation_passesWhenNoExpiry() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.PENDING)
                .build();
        validator.validateAcceptInvitation(invitation, invitee);
    }

    @Test
    void validateDeclineInvitation_throwsWhenNotInvitee() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(User.builder().id(2).build())
                .status(InvitationStatus.PENDING)
                .build();
        assertThatThrownBy(() -> validator.validateDeclineInvitation(invitation, invitee))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateDeclineInvitation_throwsWhenNotPending() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.ACCEPTED)
                .build();
        assertThatThrownBy(() -> validator.validateDeclineInvitation(invitation, invitee))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invitation is no longer pending.");
    }

    @Test
    void validateDeclineInvitation_passesWhenPendingAndInvitee() {
        GroupInvitation invitation = GroupInvitation.builder()
                .invitee(invitee)
                .status(InvitationStatus.PENDING)
                .build();
        validator.validateDeclineInvitation(invitation, invitee);
    }

    @Test
    void validateRevokeInvitation_throwsWhenCallerNotOwner() {
        GroupMember caller = GroupMember.builder().role(GroupRole.MEMBER).build();
        GroupInvitation invitation = GroupInvitation.builder().status(InvitationStatus.PENDING).build();
        assertThatThrownBy(() -> validator.validateRevokeInvitation(invitation, caller))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateRevokeInvitation_throwsWhenCallerNull() {
        GroupInvitation invitation = GroupInvitation.builder().status(InvitationStatus.PENDING).build();
        assertThatThrownBy(() -> validator.validateRevokeInvitation(invitation, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateRevokeInvitation_throwsWhenNotPending() {
        GroupMember caller = GroupMember.builder().role(GroupRole.OWNER).build();
        GroupInvitation invitation = GroupInvitation.builder().status(InvitationStatus.REVOKED).build();
        assertThatThrownBy(() -> validator.validateRevokeInvitation(invitation, caller))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only pending invitations can be revoked.");
    }

    @Test
    void validateRevokeInvitation_passesWhenOwnerAndPending() {
        GroupMember caller = GroupMember.builder().role(GroupRole.OWNER).build();
        GroupInvitation invitation = GroupInvitation.builder().status(InvitationStatus.PENDING).build();
        validator.validateRevokeInvitation(invitation, caller);
    }
}
