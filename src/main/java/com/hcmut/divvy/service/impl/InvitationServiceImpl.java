package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.helper.StringHelper;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.repository.GroupInvitationRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.service.InvitationService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.GroupValidator;
import com.hcmut.divvy.validator.InvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {

    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final InvitationMapper invitationMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final InvitationValidator invitationValidator;
    private final GroupValidator groupValidator;

    @Override
    @Transactional
    public InvitationResponse sendInvitation(SendInvitationModel model) {
        User inviter = invitationValidator.validateUserExists(model.getCurrentUsername());
        User invitee = invitationValidator.validateInviteeExists(model.getInviteeId());
        Group group = groupValidator.validateGroupExists(model.getGroupId());

        invitationValidator.validateSendInvitation(model.getGroupId(), inviter.getId(), invitee.getId());

        String token = "tok_" + StringHelper.generateRandomAlphanumeric(16);
        GroupInvitation invitation = invitationMapper.toEntity(model, group, inviter, invitee, token);

        GroupInvitation saved = groupInvitationRepository.save(invitation);
        return invitationMapper.toResponse(saved);
    }

    @Override
    public List<InvitationResponse> getGroupInvitations(GetGroupInvitationsModel model) {
        User caller = invitationValidator.validateUserExists(model.getCurrentUsername());
        groupValidator.validateIsAdmin(model.getGroupId(), caller.getId());

        List<GroupInvitation> invitations = model.getStatus() != null
                ? groupInvitationRepository.findAllByGroupIdAndStatus(model.getGroupId(), model.getStatus())
                : groupInvitationRepository.findAllByGroupId(model.getGroupId());

        return invitations.stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    public List<InvitationResponse> getMyInvitations(GetMyInvitationsModel model) {
        User caller = invitationValidator.validateUserExists(model.getCurrentUsername());

        InvitationStatus targetStatus = model.getStatus() != null ? model.getStatus() : InvitationStatus.PENDING;
        List<GroupInvitation> invitations = groupInvitationRepository.findAllByInviteeIdAndStatus(caller.getId(),
                targetStatus);

        return invitations.stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AcceptInvitationResponse acceptInvitation(AcceptInvitationModel model) {
        User caller = invitationValidator.validateUserExists(model.getCurrentUsername());
        GroupInvitation invitation = invitationValidator.validateInvitationExists(model.getInvitationId());

        invitationValidator.validateAcceptInvitation(invitation, caller);

        // Update invitation status
        invitation.setStatus(InvitationStatus.ACCEPTED);
        GroupInvitation updatedInvitation = groupInvitationRepository.save(invitation);

        // Automatically add user to group as MEMBER
        groupMemberRepository.save(groupMemberMapper.toEntity(invitation.getGroup(), caller, GroupRole.MEMBER));

        return invitationMapper.toAcceptResponse(updatedInvitation);
    }

    @Override
    @Transactional
    public InvitationStatusResponse declineInvitation(DeclineInvitationModel model) {
        User caller = invitationValidator.validateUserExists(model.getCurrentUsername());
        GroupInvitation invitation = invitationValidator.validateInvitationExists(model.getInvitationId());

        invitationValidator.validateDeclineInvitation(invitation, caller);

        invitation.setStatus(InvitationStatus.DECLINED);
        GroupInvitation updatedInvitation = groupInvitationRepository.save(invitation);

        return invitationMapper.toStatusResponse(updatedInvitation);
    }

    @Override
    @Transactional
    public InvitationStatusResponse revokeInvitation(RevokeInvitationModel model) {
        User caller = invitationValidator.validateUserExists(model.getCurrentUsername());
        GroupInvitation invitation = invitationValidator.validateInvitationExists(model.getInvitationId());

        invitationValidator.validateRevokeInvitation(invitation, caller);

        invitation.setStatus(InvitationStatus.REVOKED);
        GroupInvitation updatedInvitation = groupInvitationRepository.save(invitation);

        return invitationMapper.toStatusResponse(updatedInvitation);
    }
}
