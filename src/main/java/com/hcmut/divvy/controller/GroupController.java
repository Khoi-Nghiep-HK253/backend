package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.service.GroupService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Group Management", description = "APIs for creating, listing, updating, and deleting expense groups")
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    /**
     * Create a new expense group.
     * <p>
     * The creator is automatically added to the group with the {@code OWNER} role.
     * {@code categoryId} and {@code defaultCurrencyId} are optional.
     *
     * @param request        group payload (name, note, categoryId, defaultCurrencyId, startDate, endDate)
     * @param authentication the currently authenticated user (will become the OWNER)
     * @return {@code 201 Created} with GroupResponse
     */
    @PostMapping
    @Operation(summary = "Create a new expense group")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            Authentication authentication) {
        CreateGroupModel model = groupMapper.toModel(request, authentication.getName());
        GroupResponse group = groupService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(group, "Group created successfully"));
    }

    /**
     * Retrieve a paginated list of groups that the current user has joined.
     * <p>
     * Results are sorted by {@code createdAt} descending by default.
     *
     * @param page           page number (zero-based, default 0)
     * @param size           page size (default 20)
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with {@code Page<GroupResponse>}
     */
    @GetMapping
    @Operation(summary = "Get paginated list of groups joined by current user")
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> getMyGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        FindMyGroupsModel model = groupMapper.toFindMyGroupsModel(authentication.getName(), pageable);
        Page<GroupResponse> groups = groupService.findMyGroups(model);
        return ResponseEntity.ok(ApiResponse.ok(groups, "Groups retrieved successfully"));
    }

    /**
     * Retrieve the details of a specific group.
     * <p>
     * The caller must be a member of the group to access its details.
     *
     * @param groupId        the group's ID
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with GroupResponse;
     *         {@code 404} if the group does not exist; {@code 403} if the caller is not a member
     */
    @GetMapping("/{groupId}")
    @Operation(summary = "Get group details by ID")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(
            @PathVariable Integer groupId,
            Authentication authentication) {
        GetGroupByIdModel model = groupMapper.toGetGroupByIdModel(groupId, authentication.getName());
        GroupResponse group = groupService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(group, "Group retrieved successfully"));
    }

    /**
     * Update the group's information.
     * <p>
     * Requires the {@code OWNER} role.
     * Fields that are {@code null} in the request are left unchanged
     * (partial update via MapStruct {@code BeanMapping(nullValuePropertyMappingStrategy = IGNORE)}).
     *
     * @param groupId        the group's ID
     * @param request        fields to update (name, note, categoryId, defaultCurrencyId, startDate, endDate)
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 200 OK} with the updated GroupResponse;
     *         {@code 404} if the group does not exist; {@code 403} if the caller is not the OWNER
     */
    @PutMapping("/{groupId}")
    @Operation(summary = "Update group details (Requires OWNER role)")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Integer groupId,
            @RequestBody UpdateGroupRequest request,
            Authentication authentication) {
        UpdateGroupModel model = groupMapper.toModel(request, groupId, authentication.getName());
        GroupResponse group = groupService.update(model);
        return ResponseEntity.ok(ApiResponse.ok(group, "Group updated successfully"));
    }

    /**
     * Permanently delete a group and all of its associated data.
     * <p>
     * Requires the {@code OWNER} role.
     * Cascades to: GroupMember, Invitation, Expense, Debt, Settlement.
     * This action is irreversible.
     *
     * @param groupId        the group's ID
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 200 OK};
     *         {@code 404} if the group does not exist; {@code 403} if the caller is not the OWNER
     */
    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete an expense group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Integer groupId,
            Authentication authentication) {
        DeleteGroupModel model = groupMapper.toDeleteGroupModel(groupId, authentication.getName());
        groupService.delete(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Group deleted successfully"));
    }
}
