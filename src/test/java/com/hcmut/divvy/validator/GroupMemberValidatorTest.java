package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.GroupRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupMemberValidatorTest {

    private final GroupMemberValidator validator = new GroupMemberValidator();

    @Test
    void validateIsMember_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateIsMember(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateIsAdmin_throwsWhenNotOwner() {
        GroupMember member = GroupMember.builder().role(GroupRole.MEMBER).build();
        assertThatThrownBy(() -> validator.validateIsAdmin(member)).isInstanceOf(BusinessException.class);
    }

    @Test
    void validateIsAdmin_passesWhenOwner() {
        validator.validateIsAdmin(GroupMember.builder().role(GroupRole.OWNER).build());
    }

    @Test
    void validateAddMember_throwsWhenAlreadyMember() {
        assertThatThrownBy(() -> validator.validateAddMember(true))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateAddMember_passesWhenNotAlreadyMember() {
        validator.validateAddMember(false);
    }

    @Test
    void validateUpdateRole_throwsWhenDowngradingLastOwner() {
        GroupMember owner = GroupMember.builder().role(GroupRole.OWNER).build();
        assertThatThrownBy(() -> validator.validateUpdateRole(owner, "MEMBER", 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot downgrade the last owner of the group.");
    }

    @Test
    void validateUpdateRole_passesWhenAnotherOwnerRemains() {
        GroupMember owner = GroupMember.builder().role(GroupRole.OWNER).build();
        validator.validateUpdateRole(owner, "MEMBER", 2);
    }

    @Test
    void validateUpdateRole_passesWhenPromotingMember() {
        GroupMember member = GroupMember.builder().role(GroupRole.MEMBER).build();
        validator.validateUpdateRole(member, "OWNER", 1);
    }

    @Test
    void validateRemoveMember_throwsWhenNotSelfAndNotAdmin() {
        User target = User.builder().id(2).build();
        GroupMember targetMember = GroupMember.builder().user(target).role(GroupRole.MEMBER).build();

        assertThatThrownBy(() -> validator.validateRemoveMember(targetMember, 1, false, 1))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateRemoveMember_passesWhenRemovingSelf() {
        User target = User.builder().id(1).build();
        GroupMember targetMember = GroupMember.builder().user(target).role(GroupRole.MEMBER).build();

        validator.validateRemoveMember(targetMember, 1, false, 1);
    }

    @Test
    void validateRemoveMember_throwsWhenRemovingLastOwner() {
        User target = User.builder().id(2).build();
        GroupMember targetMember = GroupMember.builder().user(target).role(GroupRole.OWNER).build();

        assertThatThrownBy(() -> validator.validateRemoveMember(targetMember, 1, true, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot remove the last owner of the group.");
    }

    @Test
    void validateRemoveMember_passesWhenAdminRemovesNonLastOwner() {
        User target = User.builder().id(2).build();
        GroupMember targetMember = GroupMember.builder().user(target).role(GroupRole.OWNER).build();

        validator.validateRemoveMember(targetMember, 1, true, 2);
    }
}
