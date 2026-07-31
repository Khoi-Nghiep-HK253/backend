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
        groupMemberValidator.validateGroupExists(model.getGroupId());
        groupMemberValidator.validateIsMember(model.getGroupId(), caller.getId());

        return groupMemberRepository.findAllByGroupId(model.getGroupId())
                .stream()
                .map(groupMemberMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(AddMemberModel model) {
        User caller = findUser(model.getCurrentUsername());
        groupMemberValidator.validateGroupExists(model.getGroupId());
        groupMemberValidator.validateIsAdmin(model.getGroupId(), caller.getId());

        User targetUser = userRepository.findById(model.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", model.getUserId()));

        groupMemberValidator.validateAddMember(model.getGroupId(), targetUser.getId());

        Group group = findGroup(model.getGroupId());
        GroupMember newMember = groupMemberMapper.toEntity(group, targetUser, GroupRole.MEMBER);

        return groupMemberMapper.toResponse(groupMemberRepository.save(newMember));
    }

    @Override
    @Transactional
    public GroupMemberResponse updateRole(UpdateMemberRoleModel model) {
        User caller = findUser(model.getCurrentUsername());
        groupMemberValidator.validateGroupExists(model.getGroupId());
        groupMemberValidator.validateIsAdmin(model.getGroupId(), caller.getId());

        GroupMember member = groupMemberRepository.findById(model.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "id", model.getMemberId()));

        groupMemberValidator.validateUpdateRole(member, model.getRole());

        member.setRole(GroupRole.valueOf(model.getRole()));

        return groupMemberMapper.toResponse(groupMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(RemoveMemberModel model) {
        User caller = findUser(model.getCurrentUsername());
        groupMemberValidator.validateGroupExists(model.getGroupId());

        GroupMember targetMember = groupMemberRepository.findById(model.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "id", model.getMemberId()));

        boolean isCallerAdmin = groupMemberRepository.findByGroupIdAndUserId(model.getGroupId(), caller.getId())
                .map(m -> GroupRole.OWNER == m.getRole())
                .orElse(false);

        groupMemberValidator.validateRemoveMember(targetMember, caller.getId(), isCallerAdmin);

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
}
