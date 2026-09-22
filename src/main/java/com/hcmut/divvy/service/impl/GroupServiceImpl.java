package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.repository.*;
import com.hcmut.divvy.service.GroupService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.CategoryValidator;
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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupValidator groupValidator;
    private final CategoryValidator categoryValidator;

    @Override
    @Transactional
    public GroupResponse create(CreateGroupModel model) {
        User creator = findUser(model.getCurrentUsername());
        Category category = resolveCategory(model.getCategoryId(), model.getCategoryName());

        Group group = groupMapper.toEntity(model, creator, category);
        Group saved = groupRepository.save(group);

        groupMemberRepository.save(groupMemberMapper.toEntity(saved, creator, GroupRole.OWNER));

        return groupMapper.toResponse(saved);
    }

    @Override
    public Page<GroupResponse> findMyGroups(FindMyGroupsModel model) {
        User user = findUser(model.getCurrentUsername());
        return groupRepository.findAllByMemberId(user.getId(), model.getPageable())
                .map(groupMapper::toResponse);
    }

    @Override
    public GroupResponse findById(GetGroupByIdModel model) {
        User user = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember member = findMember(group.getId(), user.getId());

        groupValidator.validateIsMember(member);
        return groupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse update(UpdateGroupModel model) {
        User user = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember member = findMember(group.getId(), user.getId());

        groupValidator.validateIsAdmin(member);

        Category category = resolveCategory(model.getCategoryId(), model.getCategoryName());

        groupMapper.updateEntity(model, category, group);

        return groupMapper.toResponse(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void delete(DeleteGroupModel model) {
        User user = findUser(model.getCurrentUsername());
        Group group = findGroup(model.getGroupId());
        GroupMember member = findMember(group.getId(), user.getId());

        groupValidator.validateIsAdmin(member);
        groupRepository.delete(group);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Group findGroup(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    /**
     * Resolves the group's category, one of two ways:
     * <ul>
     * <li>{@code categoryId} — must reference an existing category, 404 otherwise.
     * <li>{@code categoryName} — matched case-insensitively against existing
     * categories, or created on the spot if none matches.
     * </ul>
     * Both are optional; if neither is given, the group has no category.
     */
    private Category resolveCategory(Integer categoryId, String categoryName) {
        groupValidator.validateCategorySelection(categoryId, categoryName);

        if (categoryId != null) {
            return categoryValidator.validateCategoryExists(categoryRepository.findById(categoryId), categoryId);
        }
        if (categoryName != null && !categoryName.isBlank()) {
            String trimmed = categoryName.trim();
            return categoryRepository.findByNameIgnoreCase(trimmed)
                    .orElseGet(() -> categoryRepository.save(Category.builder().name(trimmed).build()));
        }
        return null;
    }

    private GroupMember findMember(Integer groupId, Integer userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElse(null);
    }
}
