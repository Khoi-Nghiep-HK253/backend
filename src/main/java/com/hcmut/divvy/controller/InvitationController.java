package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.service.InvitationService;
import com.hcmut.divvy.service.model.*;
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
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    /**
     * Retrieve the invitation inbox of the currently authenticated user.
     * <p>
     * Defaults to returning only {@code PENDING} invitations when no {@code status} is provided.
     *
     * @param status         optional status filter (defaults to PENDING)
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with a list of InvitationResponse
     */
    @GetMapping("/me")
    @Operation(summary = "Get list of invitations received by current user (Inbox)")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyInvitations(
            @RequestParam(required = false) InvitationStatus status,
            Authentication authentication) {
        GetMyInvitationsModel model = invitationMapper.toGetMyInvitationsModel(status, authentication.getName());
        List<InvitationResponse> responses = invitationService.getMyInvitations(model);
        return ResponseEntity.ok(ApiResponse.ok(responses, "My invitations retrieved successfully"));
    }

    /**
     * Accept a group invitation.
     * <p>
     * Only the designated invitee may accept.
     * The invitation must be in the {@code PENDING} state and must not be expired.
     * Upon acceptance, the user is automatically added to the group with the {@code MEMBER} role.
     *
     * @param invitationId   the ID of the invitation to accept
     * @param authentication the currently authenticated user (must be the invitee)
     * @return {@code 200 OK} with AcceptInvitationResponse (invitation + membership);
     *         {@code 400} if the invitation has expired or is no longer pending;
     *         {@code 403} if the caller is not the invitee
     */
    @PutMapping("/{invitationId}/accept")
    @Operation(summary = "Accept an invitation to join a group")
    public ResponseEntity<ApiResponse<AcceptInvitationResponse>> acceptInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        AcceptInvitationModel model = invitationMapper.toAcceptInvitationModel(invitationId, authentication.getName());
        AcceptInvitationResponse response = invitationService.acceptInvitation(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation accepted successfully"));
    }

    /**
     * Decline a group invitation.
     * <p>
     * Only the designated invitee may decline.
     * The invitation must be in the {@code PENDING} state.
     * On success, the invitation status transitions to {@code DECLINED}.
     *
     * @param invitationId   the ID of the invitation to decline
     * @param authentication the currently authenticated user (must be the invitee)
     * @return {@code 200 OK} with InvitationStatusResponse {status: "DECLINED"};
     *         {@code 400} if the invitation is no longer pending;
     *         {@code 403} if the caller is not the invitee
     */
    @PutMapping("/{invitationId}/decline")
    @Operation(summary = "Decline an invitation")
    public ResponseEntity<ApiResponse<InvitationStatusResponse>> declineInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        DeclineInvitationModel model = invitationMapper.toDeclineInvitationModel(invitationId, authentication.getName());
        InvitationStatusResponse response = invitationService.declineInvitation(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation declined successfully"));
    }

    /**
     * Revoke a pending invitation (OWNER cancels an outgoing invite).
     * <p>
     * Only the {@code OWNER} of the group that issued the invitation may revoke it.
     * Only invitations in the {@code PENDING} state can be revoked.
     * On success, the invitation status transitions to {@code REVOKED}.
     *
     * @param invitationId   the ID of the invitation to revoke
     * @param authentication the currently authenticated user (must be the group OWNER)
     * @return {@code 200 OK} with InvitationStatusResponse {status: "REVOKED"};
     *         {@code 400} if the invitation is no longer pending;
     *         {@code 403} if the caller is not the OWNER
     */
    @PutMapping("/{invitationId}/revoke")
    @Operation(summary = "Revoke an invitation sent by group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<InvitationStatusResponse>> revokeInvitation(
            @PathVariable Integer invitationId,
            Authentication authentication) {
        RevokeInvitationModel model = invitationMapper.toRevokeInvitationModel(invitationId, authentication.getName());
        InvitationStatusResponse response = invitationService.revokeInvitation(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Invitation revoked successfully"));
    }
}
