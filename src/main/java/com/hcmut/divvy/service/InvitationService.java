package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.AcceptInvitationResponse;
import com.hcmut.divvy.dto.response.InvitationResponse;
import com.hcmut.divvy.dto.response.InvitationStatusResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface InvitationService {

    /**
     * Sends a group invitation to another user (requires OWNER or MEMBER role).
     *
     * @param model contains the group ID, the invitee's username, and an optional
     *              message
     * @return the created invitation record
     */
    InvitationResponse sendInvitation(SendInvitationModel model);

    /**
     * Returns all invitations sent within a specific group.
     *
     * @param model contains the group ID and the caller's username (must be a group
     *              member)
     * @return list of invitations for the group
     */
    List<InvitationResponse> getGroupInvitations(GetGroupInvitationsModel model);

    /**
     * Returns all pending invitations received by the currently authenticated user.
     *
     * @param model contains the caller's username and optional status filter
     * @return list of invitations received by the user
     */
    List<InvitationResponse> getMyInvitations(GetMyInvitationsModel model);

    /**
     * Accepts a group invitation and adds the invitee as a group member.
     *
     * @param model contains the invitation ID and the caller's username (must be
     *              the invitee)
     * @return confirmation of the accepted invitation and the new membership
     *         details
     */
    AcceptInvitationResponse acceptInvitation(AcceptInvitationModel model);

    /**
     * Declines a pending group invitation.
     *
     * @param model contains the invitation ID and the caller's username (must be
     *              the invitee)
     * @return the updated invitation status
     */
    InvitationStatusResponse declineInvitation(DeclineInvitationModel model);

    /**
     * Revokes (cancels) a previously sent invitation (requires OWNER role or the
     * original inviter).
     *
     * @param model contains the invitation ID and the caller's username
     * @return the updated invitation status
     */
    InvitationStatusResponse revokeInvitation(RevokeInvitationModel model);

    /**
     * Retrieves invitation details by its unique token (public endpoint for email
     * links).
     *
     * @param token the unique invitation token from the email link
     * @return invitation details; throws 404 if the token is invalid
     */
    InvitationResponse getInvitationByToken(String token);

    /**
     * Accepts an invitation via its token link (used from the email invite link
     * flow).
     *
     * @param token           the unique invitation token from the email link
     * @param currentUsername the username of the authenticated user accepting the
     *                        invitation
     * @return confirmation of the accepted invitation and the new membership
     *         details
     */
    AcceptInvitationResponse acceptInvitationByToken(String token, String currentUsername);
}
