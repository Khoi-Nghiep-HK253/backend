package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.AddMemberRequest;
import com.hcmut.divvy.dto.request.UpdateMemberRoleRequest;
import com.hcmut.divvy.dto.response.GroupMemberResponse;
import com.hcmut.divvy.mapper.GroupMemberMapper;
import com.hcmut.divvy.service.GroupMemberService;
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
public class GroupMemberController {

        private final GroupMemberService groupMemberService;
        private final GroupMemberMapper groupMemberMapper;

        @GetMapping
        public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getMembers(
                        @PathVariable Integer groupId,
                        Authentication authentication) {
                List<GroupMemberResponse> members = groupMemberService.getMembers(
                                groupMemberMapper.toGetMembersModel(groupId, authentication.getName()));
                return ResponseEntity.ok(ApiResponse.ok(members, "Group members retrieved successfully"));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<GroupMemberResponse>> addMember(
                        @PathVariable Integer groupId,
                        @Valid @RequestBody AddMemberRequest request,
                        Authentication authentication) {
                GroupMemberResponse member = groupMemberService.addMember(
                                groupMemberMapper.toModel(request, groupId, authentication.getName()));
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.created(member, "Member added to group successfully"));
        }

        @PutMapping("/{memberId}/role")
        public ResponseEntity<ApiResponse<GroupMemberResponse>> updateRole(
                        @PathVariable Integer groupId,
                        @PathVariable Integer memberId,
                        @Valid @RequestBody UpdateMemberRoleRequest request,
                        Authentication authentication) {
                GroupMemberResponse member = groupMemberService.updateRole(
                                groupMemberMapper.toModel(request, groupId, memberId, authentication.getName()));
                return ResponseEntity.ok(ApiResponse.ok(member, "Member role updated successfully"));
        }

        @DeleteMapping("/{memberId}")
        public ResponseEntity<ApiResponse<Void>> removeMember(
                        @PathVariable Integer groupId,
                        @PathVariable Integer memberId,
                        Authentication authentication) {
                groupMemberService.removeMember(
                                groupMemberMapper.toRemoveMemberModel(groupId, memberId, authentication.getName()));
                return ResponseEntity.ok(ApiResponse.ok(null, "Member removed from group successfully"));
        }
}
