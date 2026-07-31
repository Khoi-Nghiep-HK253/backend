package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcmut.divvy.service.GroupService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;
    private final GroupMapper groupMapper;

    @Override
    @Transactional
    public GroupResponse create(CreateGroupRequest request, String currentUsername) {
        User creator = findUser(currentUsername);

        Group group = Group.builder()
                .name(request.getName())
                .note(request.getNote())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(creator)
                .build();

        if (request.getCategoryId() != null) {
            group.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId())));
        }
        if (request.getDefaultCurrencyId() != null) {
            group.setDefaultCurrency(currencyRepository.findById(request.getDefaultCurrencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", request.getDefaultCurrencyId())));
        }

        Group saved = groupRepository.save(group);

        // Creator automatically becomes ADMIN member
        groupMemberRepository.save(GroupMember.builder()
                .group(saved)
                .user(creator)
                .role("ADMIN")
                .build());

        return groupMapper.toResponse(saved);
    }

    @Override
    public Page<GroupResponse> findMyGroups(String currentUsername, Pageable pageable) {
        User user = findUser(currentUsername);
        return groupRepository.findAllByMemberId(user.getId(), pageable)
                .map(groupMapper::toResponse);
    }

    @Override
    public GroupResponse findById(Integer groupId, String currentUsername) {
        User user = findUser(currentUsername);
        Group group = findGroup(groupId);
        requireMember(groupId, user.getId());
        return groupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse update(Integer groupId, UpdateGroupRequest request, String currentUsername) {
        User user = findUser(currentUsername);
        Group group = findGroup(groupId);
        requireAdmin(groupId, user.getId());

        if (request.getName() != null)         group.setName(request.getName());
        if (request.getNote() != null)         group.setNote(request.getNote());
        if (request.getStartDate() != null)    group.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)      group.setEndDate(request.getEndDate());

        if (request.getCategoryId() != null) {
            group.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId())));
        }
        if (request.getDefaultCurrencyId() != null) {
            group.setDefaultCurrency(currencyRepository.findById(request.getDefaultCurrencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", request.getDefaultCurrencyId())));
        }

        return groupMapper.toResponse(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void delete(Integer groupId, String currentUsername) {
        User user = findUser(currentUsername);
        findGroup(groupId); // ensure exists
        requireAdmin(groupId, user.getId());
        groupRepository.deleteById(groupId);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Group findGroup(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    private void requireMember(Integer groupId, Integer userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    private void requireAdmin(Integer groupId, Integer userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));
        if (!"ADMIN".equals(member.getRole())) {
            throw new BusinessException("Only group admins can perform this action.", HttpStatus.FORBIDDEN);
        }
    }
}
