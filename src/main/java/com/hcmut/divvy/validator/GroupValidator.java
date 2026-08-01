package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.GroupRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroupValidator {

    public Group validateGroupExists(Optional<Group> groupOptional, Integer groupId) {
        return groupOptional.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

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
