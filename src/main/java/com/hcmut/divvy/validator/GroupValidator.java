package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.GroupRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GroupValidator {

    public void validateIsMember(GroupMember member) {
        if (member == null) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateIsAdmin(GroupMember member) {
        validateIsMember(member);
        if (GroupRole.OWNER != member.getRole()) {
            throw new BusinessException("Only group admins can perform this action.", HttpStatus.FORBIDDEN);
        }
    }
}
