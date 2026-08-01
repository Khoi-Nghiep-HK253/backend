package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @PostMapping
    @Operation(summary = "Create a new expense group")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            Authentication authentication) {
        GroupResponse group = groupService.create(groupMapper.toModel(request, authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(group, "Group created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of groups joined by current user")
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> getMyGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Page<GroupResponse> groups = groupService.findMyGroups(
                groupMapper.toFindMyGroupsModel(
                        authentication.getName(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending())));
        return ResponseEntity.ok(ApiResponse.ok(groups, "Groups retrieved successfully"));
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "Get group details by ID")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(
            @PathVariable Integer groupId,
            Authentication authentication) {
        GroupResponse group = groupService.findById(groupMapper.toGetGroupByIdModel(groupId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(group, "Group retrieved successfully"));
    }

    @PutMapping("/{groupId}")
    @Operation(summary = "Update group details (Requires OWNER role)")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Integer groupId,
            @RequestBody UpdateGroupRequest request,
            Authentication authentication) {
        GroupResponse group = groupService.update(groupMapper.toModel(request, groupId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(group, "Group updated successfully"));
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete an expense group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Integer groupId,
            Authentication authentication) {
        groupService.delete(groupMapper.toDeleteGroupModel(groupId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Group deleted successfully"));
    }
}
