package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "My Invitations", description = "APIs for viewing received invitations, accepting, declining, and revoking invitations")
public class UserInvitationController {

    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    @GetMapping("/me")
    @Operation(summary = "Get list of invitations received by current user (Inbox)")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyInvitations(
            @RequestParam(required = false) InvitationStatus status,
            Authentication authentication) {
        List<InvitationResponse> responses = invitationService.getMyInvitations(
                invitationMapper.toGetMyInvitationsModel(status, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(responses, "My invitations retrieved successfully"));
    }

    @PutMapping("/{invitationId}/accept")
    @Operation(summary = "Accept an invitation to join a group")
    public ResponseEntity<ApiResponse<AcceptInvitationResponse>> acceptInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        AcceptInvitationResponse response = invitationService.acceptInvitation(
                invitationMapper.toAcceptInvitationModel(invitationId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation accepted successfully"));
    }

    @PutMapping("/{invitationId}/decline")
    @Operation(summary = "Decline an invitation")
    public ResponseEntity<ApiResponse<InvitationStatusResponse>> declineInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        InvitationStatusResponse response = invitationService.declineInvitation(
                invitationMapper.toDeclineInvitationModel(invitationId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation declined successfully"));
    }

    @PutMapping("/{invitationId}/revoke")
    @Operation(summary = "Revoke an invitation sent by group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<InvitationStatusResponse>> revokeInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        InvitationStatusResponse response = invitationService.revokeInvitation(
                invitationMapper.toRevokeInvitationModel(invitationId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation revoked successfully"));
    }
}
