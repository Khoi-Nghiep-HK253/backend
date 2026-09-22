package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.GroupRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupValidatorTest {

    private final GroupValidator validator = new GroupValidator();

    @Test
    void validateGroupExists_returnsGroupWhenPresent() {
        Group group = Group.builder().id(1).name("Trip").build();
        assertThat(validator.validateGroupExists(Optional.of(group), 1)).isEqualTo(group);
    }

    @Test
    void validateGroupExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateGroupExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateIsMember_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateIsMember(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateIsMember_passesWhenPresent() {
        validator.validateIsMember(GroupMember.builder().role(GroupRole.MEMBER).build());
    }

    @Test
    void validateIsAdmin_throwsWhenNotOwner() {
        GroupMember member = GroupMember.builder().role(GroupRole.MEMBER).build();
        assertThatThrownBy(() -> validator.validateIsAdmin(member))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateIsAdmin_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateIsAdmin(null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateIsAdmin_passesWhenOwner() {
        validator.validateIsAdmin(GroupMember.builder().role(GroupRole.OWNER).build());
    }

    @Test
    void validateCategorySelection_throwsWhenBothProvided() {
        assertThatThrownBy(() -> validator.validateCategorySelection(1, "Du lịch"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateCategorySelection_passesWhenOnlyIdProvided() {
        validator.validateCategorySelection(1, null);
    }

    @Test
    void validateCategorySelection_passesWhenOnlyNameProvided() {
        validator.validateCategorySelection(null, "Du lịch");
    }

    @Test
    void validateCategorySelection_passesWhenNeitherProvided() {
        validator.validateCategorySelection(null, null);
    }

    @Test
    void validateCategorySelection_passesWhenNameBlank() {
        validator.validateCategorySelection(1, "  ");
    }
}
