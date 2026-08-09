package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.SendInvitationRequest;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import com.hcmut.divvy.mapper.InvitationMapper;
import com.hcmut.divvy.service.InvitationService;
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
@RequestMapping("/api/groups/{groupId}/invitations")
@RequiredArgsConstructor
@Tag(name = "Group Invitation Management", description = "APIs for sending group invitations and viewing invitations sent by a group")
public class GroupInvitationController {

    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;

    /**
     * Send a group invitation to a specific user.
     * <p>
     * Requires the {@code OWNER} role.
     * The invitee must not already be a member and must not have an existing
     * {@code PENDING} invitation for this group.
     * An optional {@code message} and {@code expiresAt} timestamp may be included.
     *
     * @param groupId        the group's ID
     * @param request        invitation payload (inviteeId, message, expiresAt)
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 201 Created} with InvitationResponse;
     *         {@code 400} if the invitee is already a member or already has a
     *         pending invitation;
     *         {@code 403} if the caller is not the OWNER
     */
    @PostMapping
    @Operation(summary = "Send a group invitation to a user (Requires OWNER role)")
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @PathVariable Integer groupId,
            @Valid @RequestBody SendInvitationRequest request,
            Authentication authentication) {
        SendInvitationModel model = invitationMapper.toModel(request, groupId, authentication.getName());
        InvitationResponse response = invitationService.sendInvitation(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Invitation sent successfully"));
    }

    /**
     * Retrieve the list of invitations sent by the group (outbox).
     * <p>
     * Requires the {@code OWNER} role.
     * Results may be filtered by invitation status
     * ({@code PENDING}, {@code ACCEPTED}, {@code DECLINED}, {@code REVOKED},
     * {@code EXPIRED}).
     *
     * @param groupId        the group's ID
     * @param status         optional status filter
     * @param authentication the currently authenticated user (must be OWNER)
     * @return {@code 200 OK} with a list of InvitationResponse;
     *         {@code 403} if the caller is not the OWNER
     */
    @GetMapping
    @Operation(summary = "Get list of invitations sent by group (Requires OWNER role)")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getGroupInvitations(
            @PathVariable Integer groupId,
            @RequestParam(required = false) InvitationStatus status,
            Authentication authentication) {
        GetGroupInvitationsModel model = invitationMapper.toGetGroupInvitationsModel(groupId, status,
                authentication.getName());
        List<InvitationResponse> responses = invitationService.getGroupInvitations(model);
        return ResponseEntity.ok(ApiResponse.ok(responses, "Invitations retrieved successfully"));
    }
}
