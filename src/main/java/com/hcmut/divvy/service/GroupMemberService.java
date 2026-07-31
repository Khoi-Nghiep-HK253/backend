package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface GroupMemberService {

    List<GroupMemberResponse> getMembers(GetMembersModel model);

    GroupMemberResponse addMember(AddMemberModel model);

    GroupMemberResponse updateRole(UpdateMemberRoleModel model);

    void removeMember(RemoveMemberModel model);
}
