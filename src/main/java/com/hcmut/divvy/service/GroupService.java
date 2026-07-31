package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupService {

    /**
     * Creates a new group. The authenticated user becomes the ADMIN member automatically.
     *
     * @param request         group creation data
     * @param currentUsername username of the authenticated user
     */
    GroupResponse create(CreateGroupRequest request, String currentUsername);

    /**
     * Returns all groups that the authenticated user is a member of.
     *
     * @param currentUsername username of the authenticated user
     * @param pageable        pagination params
     */
    Page<GroupResponse> findMyGroups(String currentUsername, Pageable pageable);

    /**
     * Returns a single group by ID.
     * Caller must be a member of the group.
     *
     * @param groupId         the group's ID
     * @param currentUsername username of the authenticated user
     */
    GroupResponse findById(Integer groupId, String currentUsername);

    /**
     * Updates group details. Caller must be an ADMIN of the group.
     *
     * @param groupId         the group's ID
     * @param request         fields to update (null = keep existing)
     * @param currentUsername username of the authenticated user
     */
    GroupResponse update(Integer groupId, UpdateGroupRequest request, String currentUsername);

    /**
     * Deletes a group and all related data. Caller must be the ADMIN of the group.
     *
     * @param groupId         the group's ID
     * @param currentUsername username of the authenticated user
     */
    void delete(Integer groupId, String currentUsername);
}
