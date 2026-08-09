package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.service.model.*;
import org.springframework.data.domain.Page;

public interface GroupService {

    /**
     * Creates a new expense-splitting group and automatically adds the creator as
     * OWNER.
     *
     * @param model group details (name, note, category, date range) and the
     *              creator's username
     * @return the newly created group
     */
    GroupResponse create(CreateGroupModel model);

    /**
     * Returns a paginated list of groups that the current user has joined.
     *
     * @param model contains the caller's username and pagination settings
     * @return page of groups the user is a member of
     */
    Page<GroupResponse> findMyGroups(FindMyGroupsModel model);

    /**
     * Returns the details of a specific group.
     * <p>
     * The caller must be a member of the group to access its details.
     *
     * @param model contains the group ID and the caller's username
     * @return group details; throws 403 if not a member, 404 if not found
     */
    GroupResponse findById(GetGroupByIdModel model);

    /**
     * Updates a group's information (requires OWNER role).
     * <p>
     * Fields that are {@code null} in the model are left unchanged (partial
     * update).
     *
     * @param model updated group fields and the caller's username
     * @return the updated group
     */
    GroupResponse update(UpdateGroupModel model);

    /**
     * Permanently deletes a group and all of its associated data (requires OWNER
     * role).
     * <p>
     * Cascades to: GroupMember, Invitation, Expense, Debt, Settlement.
     *
     * @param model contains the group ID and the caller's username
     */
    void delete(DeleteGroupModel model);
}
