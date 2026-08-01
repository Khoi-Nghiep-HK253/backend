package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.GroupMemberService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.GroupMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupMemberValidator groupMemberValidator;

    @Override
    public List<GroupMemberResponse> getMembers(GetMembersModel model) {
        User caller = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember callerMember = findMember(group.getId(), caller.getId());

        groupMemberValidator.validateIsMember(callerMember);

        return groupMemberRepository.findAllByGroupId(group.getId())
                .stream()
                .map(groupMemberMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(AddMemberModel model) {
        User caller = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember callerMember = findMember(group.getId(), caller.getId());

        groupMemberValidator.validateIsAdmin(callerMember);

        User targetUser = userRepository.findById(model.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", model.getUserId()));

        boolean isAlreadyMember = groupMemberRepository.existsByGroupIdAndUserId(group.getId(), targetUser.getId());
        groupMemberValidator.validateAddMember(isAlreadyMember);

        GroupMember newMember = groupMemberMapper.toEntity(group, targetUser, GroupRole.MEMBER);

        return groupMemberMapper.toResponse(groupMemberRepository.save(newMember));
    }

    @Override
    @Transactional
    public GroupMemberResponse updateRole(UpdateMemberRoleModel model) {
        User caller = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember callerMember = findMember(group.getId(), caller.getId());

        groupMemberValidator.validateIsAdmin(callerMember);

        GroupMember member = groupMemberRepository.findById(model.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "id", model.getMemberId()));

        long ownerCount = groupMemberRepository.countByGroupIdAndRole(member.getGroup().getId(), GroupRole.OWNER);
        groupMemberValidator.validateUpdateRole(member, model.getRole(), ownerCount);

        member.setRole(GroupRole.valueOf(model.getRole()));

        return groupMemberMapper.toResponse(groupMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(RemoveMemberModel model) {
        User caller = findUser(model.getCurrentUsername());
        findGroup(model.getGroupId());

        GroupMember targetMember = groupMemberRepository.findById(model.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "id", model.getMemberId()));

        GroupMember callerMember = findMember(model.getGroupId(), caller.getId());
        boolean isCallerAdmin = callerMember != null && GroupRole.OWNER == callerMember.getRole();
        long ownerCount = groupMemberRepository.countByGroupIdAndRole(targetMember.getGroup().getId(), GroupRole.OWNER);

        groupMemberValidator.validateRemoveMember(targetMember, caller.getId(), isCallerAdmin, ownerCount);

        groupMemberRepository.delete(targetMember);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Group findGroup(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    private GroupMember findMember(Integer groupId, Integer userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElse(null);
    }
}
