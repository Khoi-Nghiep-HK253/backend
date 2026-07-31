package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.repository.*;
import com.hcmut.divvy.service.GroupService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupValidator groupValidator;

    @Override
    @Transactional
    public GroupResponse create(CreateGroupModel model) {
        User creator = groupValidator.validateUserExists(model.getCurrentUsername());
        Category category = groupValidator.validateCategoryExists(model.getCategoryId());
        Currency currency = groupValidator.validateCurrencyExists(model.getDefaultCurrencyId());

        Group group = groupMapper.toEntity(model, creator, category, currency);
        Group saved = groupRepository.save(group);

        groupMemberRepository.save(groupMemberMapper.toEntity(saved, creator, GroupRole.OWNER));

        return groupMapper.toResponse(saved);
    }

    @Override
    public Page<GroupResponse> findMyGroups(FindMyGroupsModel model) {
        User user = groupValidator.validateUserExists(model.getCurrentUsername());
        return groupRepository.findAllByMemberId(user.getId(), model.getPageable())
                .map(groupMapper::toResponse);
    }

    @Override
    public GroupResponse findById(GetGroupByIdModel model) {
        User user = groupValidator.validateUserExists(model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(model.getGroupId());
        groupValidator.validateIsMember(model.getGroupId(), user.getId());
        return groupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse update(UpdateGroupModel model) {
        User user = groupValidator.validateUserExists(model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(model.getGroupId());
        groupValidator.validateIsAdmin(model.getGroupId(), user.getId());

        Category category = groupValidator.validateCategoryExists(model.getCategoryId());
        Currency currency = groupValidator.validateCurrencyExists(model.getDefaultCurrencyId());

        groupMapper.updateEntity(model, category, currency, group);

        return groupMapper.toResponse(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void delete(DeleteGroupModel model) {
        User user = groupValidator.validateUserExists(model.getCurrentUsername());
        groupValidator.validateGroupExists(model.getGroupId());
        groupValidator.validateIsAdmin(model.getGroupId(), user.getId());
        groupRepository.deleteById(model.getGroupId());
    }
}
