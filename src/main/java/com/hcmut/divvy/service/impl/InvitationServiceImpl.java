package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.helper.StringHelper;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.repository.GroupInvitationRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.EmailService;
import com.hcmut.divvy.service.InvitationService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.GroupValidator;
import com.hcmut.divvy.validator.InvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final InvitationMapper invitationMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final InvitationValidator invitationValidator;
    private final GroupValidator groupValidator;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Transactional
    public InvitationResponse sendInvitation(SendInvitationModel model) {
        User inviter = findUserByUsername(model.getCurrentUsername());
        User invitee = findUserById(model.getInviteeId());
        Group group = findGroupById(model.getGroupId());

        GroupMember inviterMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), inviter.getId()).orElse(null);
        boolean isAlreadyMember = groupMemberRepository.existsByGroupIdAndUserId(group.getId(), invitee.getId());
        boolean hasPending = groupInvitationRepository.existsByGroupIdAndInviteeIdAndStatus(group.getId(), invitee.getId(), InvitationStatus.PENDING);

        invitationValidator.validateSendInvitation(inviterMember, isAlreadyMember, hasPending);

        String token = "tok_" + StringHelper.generateRandomAlphanumeric(16);
        GroupInvitation invitation = invitationMapper.toEntity(model, group, inviter, invitee, token);

        GroupInvitation saved = groupInvitationRepository.save(invitation);

        // Send Group Invitation Email asynchronously
        String inviteLink = frontendUrl + "/invitations/accept?token=" + saved.getToken();
        String inviterDisplayName = inviter.getFirstname() != null ? inviter.getFirstname() + " " + inviter.getLastname() : inviter.getUsername();
        emailService.sendGroupInvitationEmail(invitee.getEmail(), inviterDisplayName, group.getName(), inviteLink, saved.getMessage());

        return invitationMapper.toResponse(saved);
    }


    @Override
    public List<InvitationResponse> getGroupInvitations(GetGroupInvitationsModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(model.getGroupId(), caller.getId()).orElse(null);
        groupValidator.validateIsAdmin(callerMember);

        List<GroupInvitation> invitations = model.getStatus() != null
                ? groupInvitationRepository.findAllByGroupIdAndStatus(model.getGroupId(), model.getStatus())
                : groupInvitationRepository.findAllByGroupId(model.getGroupId());

        return invitations.stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    public List<InvitationResponse> getMyInvitations(GetMyInvitationsModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());

        List<GroupInvitation> invitations = model.getStatus() != null
                ? groupInvitationRepository.findAllByInviteeIdAndStatus(caller.getId(), model.getStatus())
                : groupInvitationRepository.findAllByInviteeId(caller.getId());

        return invitations.stream()
                .map(invitationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AcceptInvitationResponse acceptInvitation(AcceptInvitationModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        GroupInvitation invitation = findInvitationById(model.getInvitationId());

        if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            groupInvitationRepository.save(invitation);
            throw new BusinessException("Invitation has expired.", HttpStatus.BAD_REQUEST);
        }

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
        User caller = findUserByUsername(model.getCurrentUsername());
        GroupInvitation invitation = findInvitationById(model.getInvitationId());

        invitationValidator.validateDeclineInvitation(invitation, caller);

        invitation.setStatus(InvitationStatus.DECLINED);
        GroupInvitation updatedInvitation = groupInvitationRepository.save(invitation);

        return invitationMapper.toStatusResponse(updatedInvitation);
    }

    @Override
    @Transactional
    public InvitationStatusResponse revokeInvitation(RevokeInvitationModel model) {
        User caller = findUserByUsername(model.getCurrentUsername());
        GroupInvitation invitation = findInvitationById(model.getInvitationId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(invitation.getGroup().getId(), caller.getId()).orElse(null);
        invitationValidator.validateRevokeInvitation(invitation, callerMember);

        invitation.setStatus(InvitationStatus.REVOKED);
        GroupInvitation updatedInvitation = groupInvitationRepository.save(invitation);

        return invitationMapper.toStatusResponse(updatedInvitation);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Group findGroupById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
    }

    private GroupInvitation findInvitationById(Integer id) {
        return groupInvitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", id));
    }
}
