package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.SendInvitationRequest;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.service.InvitationService;
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
@RequestMapping("/api/groups/{groupId}/invitations")
@RequiredArgsConstructor
@Tag(name = "Group Invitation Management", description = "APIs for sending group invitations and viewing invitations sent by a group")
public class GroupInvitationController {

    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @PostMapping
    @Operation(summary = "Send a group invitation to a user (Requires OWNER role)")
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @PathVariable Integer groupId,
            @Valid @RequestBody SendInvitationRequest request,
            Authentication authentication) {
        InvitationResponse response = invitationService.sendInvitation(
                invitationMapper.toModel(request, groupId, authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Invitation sent successfully"));
    }

    @GetMapping
    @Operation(summary = "Get list of invitations sent by group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getGroupInvitations(
            @PathVariable Integer groupId,
            @RequestParam(required = false) InvitationStatus status,
            Authentication authentication) {
        List<InvitationResponse> responses = invitationService.getGroupInvitations(
                invitationMapper.toGetGroupInvitationsModel(groupId, status, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(responses, "Invitations retrieved successfully"));
    }
}
