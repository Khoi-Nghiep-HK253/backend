package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.AddMemberRequest;
import com.hcmut.divvy.dto.request.UpdateMemberRoleRequest;
import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.service.GroupMemberService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@RequiredArgsConstructor
@Tag(name = "Group Member Management", description = "APIs for listing group members, adding/removing members, and updating roles")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;
    private final GroupMemberMapper groupMemberMapper;

    /**
     * Retrieve all members of a group.
     * <p>
     * The caller must be a member of the group.
     *
     * @param groupId        the group's ID
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with a list of GroupMemberResponse;
     *         {@code 403} if the caller is not a member; {@code 404} if the group does not exist
     */
    @GetMapping
    @Operation(summary = "Get list of all members in a group")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getMembers(
            @PathVariable Integer groupId,
            Authentication authentication) {
        GetMembersModel model = groupMemberMapper.toGetMembersModel(groupId, authentication.getName());
        List<GroupMemberResponse> members = groupMemberService.getMembers(model);
        return ResponseEntity.ok(ApiResponse.ok(members, "Group members retrieved successfully"));
    }

    /**
     * Directly add a user to the group by their ID.
     * <p>
     * Requires the {@code OWNER} role.
     * The new member is assigned the {@code MEMBER} role by default.
     * For an invitation-based flow, use the Invitation API instead.
     *
     * @param groupId        the group's ID
     * @param request        the payload containing the target user's ID
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 201 Created} with GroupMemberResponse;
     *         {@code 400} if the user is already a member; {@code 403} if the caller is not the OWNER
     */
    @PostMapping
    @Operation(summary = "Add a new member to group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> addMember(
            @PathVariable Integer groupId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication authentication) {
        AddMemberModel model = groupMemberMapper.toModel(request, groupId, authentication.getName());
        GroupMemberResponse member = groupMemberService.addMember(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(member, "Member added to group successfully"));
    }

    /**
     * Update a member's role within the group ({@code OWNER} ↔ {@code MEMBER}).
     * <p>
     * Requires the {@code OWNER} role.
     * The sole {@code OWNER} cannot be downgraded to {@code MEMBER};
     * the group must always have at least one OWNER.
     *
     * @param groupId        the group's ID
     * @param memberId       the ID of the GroupMember record to update
     * @param request        the new role (OWNER / MEMBER)
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 200 OK} with the updated GroupMemberResponse;
     *         {@code 400} if attempting to downgrade the last OWNER;
     *         {@code 403} if the caller is not the OWNER
     */
    @PutMapping("/{memberId}/role")
    @Operation(summary = "Update member role (OWNER / MEMBER) (Requires OWNER role)")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> updateRole(
            @PathVariable Integer groupId,
            @PathVariable Integer memberId,
            @Valid @RequestBody UpdateMemberRoleRequest request,
            Authentication authentication) {
        UpdateMemberRoleModel model = groupMemberMapper.toModel(request, groupId, memberId, authentication.getName());
        GroupMemberResponse member = groupMemberService.updateRole(model);
        return ResponseEntity.ok(ApiResponse.ok(member, "Member role updated successfully"));
    }

    /**
     * Remove a member from the group, or leave the group voluntarily.
     * <p>
     * Authorization rules:
     * <ul>
     *   <li>Any member may remove themselves (leave).</li>
     *   <li>Only an {@code OWNER} may remove other members.</li>
     *   <li>The sole {@code OWNER} cannot be removed.</li>
     * </ul>
     *
     * @param groupId        the group's ID
     * @param memberId       the ID of the GroupMember record to remove
     * @param authentication the currently authenticated user
     * @return {@code 200 OK};
     *         {@code 400} if attempting to remove the last OWNER;
     *         {@code 403} if the caller is not authorized to remove this member
     */
    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove a member from group or leave group")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Integer groupId,
            @PathVariable Integer memberId,
            Authentication authentication) {
        RemoveMemberModel model = groupMemberMapper.toRemoveMemberModel(groupId, memberId, authentication.getName());
        groupMemberService.removeMember(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Member removed from group successfully"));
    }
}
