package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateGroupRequest;
import com.hcmut.divvy.dto.request.UpdateGroupRequest;
import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.facade.GroupFacade;
import com.hcmut.divvy.service.GroupService;
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
public class GroupController {

    private final GroupFacade groupFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            Authentication authentication) {
        GroupResponse group = groupFacade.execute(GroupService.class,
                service -> service.create(request, authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(group, "Group created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> getMyGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Page<GroupResponse> groups = groupFacade.execute(GroupService.class,
                service -> service.findMyGroups(
                        authentication.getName(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending())));
        return ResponseEntity.ok(ApiResponse.ok(groups, "Groups retrieved successfully"));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(
            @PathVariable Integer groupId,
            Authentication authentication) {
        GroupResponse group = groupFacade.execute(GroupService.class,
                service -> service.findById(groupId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(group, "Group retrieved successfully"));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Integer groupId,
            @RequestBody UpdateGroupRequest request,
            Authentication authentication) {
        GroupResponse group = groupFacade.execute(GroupService.class,
                service -> service.update(groupId, request, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(group, "Group updated successfully"));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Integer groupId,
            Authentication authentication) {
        groupFacade.executeVoid(GroupService.class,
                service -> service.delete(groupId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Group deleted successfully"));
    }
}
