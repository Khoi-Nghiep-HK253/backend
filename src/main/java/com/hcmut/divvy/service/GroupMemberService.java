package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface GroupMemberService {

    /**
     * Returns the list of all members in a group.
     *
     * @param model contains the group ID and the caller's username (must be a
     *              member)
     * @return list of member profiles with their roles
     */
    List<GroupMemberResponse> getMembers(GetMembersModel model);

    /**
     * Adds a new member to a group (requires OWNER role).
     *
     * @param model contains the group ID, the target user's username, and the
     *              caller's username
     * @return the newly added member's profile and role
     */
    GroupMemberResponse addMember(AddMemberModel model);

    /**
     * Updates an existing member's role within a group (requires OWNER role).
     *
     * @param model contains the group ID, member ID, new role, and the caller's
     *              username
     * @return the updated member profile with the new role
     */
    GroupMemberResponse updateRole(UpdateMemberRoleModel model);

    /**
     * Removes a member from a group (requires OWNER role or self-removal).
     *
     * @param model contains the group ID, the target member's ID, and the caller's
     *              username
     */
    void removeMember(RemoveMemberModel model);
}
