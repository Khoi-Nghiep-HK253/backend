package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateShareLinkRequest;
import com.hcmut.divvy.dto.response.GroupPreviewResponse;
import com.hcmut.divvy.dto.response.ShareLinkResponse;
import com.hcmut.divvy.mapper.GroupShareLinkMapper;
import com.hcmut.divvy.service.GroupShareLinkService;
import com.hcmut.divvy.service.model.CreateShareLinkModel;
import com.hcmut.divvy.service.model.GetGroupPreviewModel;
import com.hcmut.divvy.service.model.GetGroupShareLinksModel;
import com.hcmut.divvy.service.model.JoinViaLinkModel;
import com.hcmut.divvy.service.model.RevokeShareLinkModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Group Share Link Management", description = "APIs for creating, revoking, previewing, and joining groups via shareable links")
public class GroupShareLinkController {

    private final GroupShareLinkService shareLinkService;
    private final GroupShareLinkMapper shareLinkMapper;

    @PostMapping("/api/groups/{groupId}/share-links")
    @Operation(summary = "Create a new shareable link for a group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> createShareLink(
            @PathVariable Integer groupId,
            @RequestBody(required = false) CreateShareLinkRequest request,
            Authentication authentication) {
        CreateShareLinkModel model = shareLinkMapper.toCreateShareLinkModel(request, groupId, authentication.getName());
        ShareLinkResponse response = shareLinkService.createShareLink(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Group share link created successfully"));
    }

    @GetMapping("/api/groups/{groupId}/share-links")
    @Operation(summary = "Get all shareable links for a group (Requires MEMBER role)")
    public ResponseEntity<ApiResponse<List<ShareLinkResponse>>> getGroupShareLinks(
            @PathVariable Integer groupId,
            Authentication authentication) {
        GetGroupShareLinksModel model = shareLinkMapper.toGetGroupShareLinksModel(groupId, authentication.getName());
        List<ShareLinkResponse> responses = shareLinkService.getGroupShareLinks(model);
        return ResponseEntity.ok(ApiResponse.ok(responses, "Retrieved group share links successfully"));
    }

    @DeleteMapping("/api/groups/{groupId}/share-links/{linkId}")
    @Operation(summary = "Revoke a group share link (Requires OWNER role)")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> revokeShareLink(
            @PathVariable Integer groupId,
            @PathVariable Integer linkId,
            Authentication authentication) {
        RevokeShareLinkModel model = shareLinkMapper.toRevokeShareLinkModel(groupId, linkId, authentication.getName());
        ShareLinkResponse response = shareLinkService.revokeShareLink(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Group share link revoked successfully"));
    }

    @GetMapping("/api/groups/join-via-link/preview/{inviteCode}")
    @Operation(summary = "Get group preview info via share invite code (Public Endpoint)")
    public ResponseEntity<ApiResponse<GroupPreviewResponse>> getGroupPreview(
            @PathVariable String inviteCode) {
        GetGroupPreviewModel model = shareLinkMapper.toGetGroupPreviewModel(inviteCode);
        GroupPreviewResponse response = shareLinkService.getGroupPreview(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Retrieved group preview successfully"));
    }

    @PostMapping("/api/groups/join-via-link/{inviteCode}")
    @Operation(summary = "Join group via share invite code (Authenticated User)")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> joinGroupViaLink(
            @PathVariable String inviteCode,
            Authentication authentication) {
        JoinViaLinkModel model = shareLinkMapper.toJoinViaLinkModel(inviteCode, authentication.getName());
        ShareLinkResponse response = shareLinkService.joinGroupViaLink(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Joined group successfully via share link"));
    }
}
