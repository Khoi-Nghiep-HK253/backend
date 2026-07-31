package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GroupValidator {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;

    public User validateUserExists(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public Group validateGroupExists(Integer groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    public Category validateCategoryExists(Integer categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    public Currency validateCurrencyExists(Integer currencyId) {
        if (currencyId == null) return null;
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));
    }

    public void validateIsMember(Integer groupId, Integer userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateIsAdmin(Integer groupId, Integer userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN));
        if (GroupRole.OWNER != member.getRole()) {
            throw new BusinessException("Only group admins can perform this action.", HttpStatus.FORBIDDEN);
        }
    }


}
