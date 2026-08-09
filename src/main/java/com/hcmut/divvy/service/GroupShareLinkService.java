package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.GroupPreviewResponse;
import com.hcmut.divvy.dto.response.ShareLinkResponse;
import com.hcmut.divvy.service.model.CreateShareLinkModel;
import com.hcmut.divvy.service.model.GetGroupPreviewModel;
import com.hcmut.divvy.service.model.GetGroupShareLinksModel;
import com.hcmut.divvy.service.model.JoinViaLinkModel;
import com.hcmut.divvy.service.model.RevokeShareLinkModel;

import java.util.List;

public interface GroupShareLinkService {

    /**
     * Creates a new shareable invite link for a group (requires OWNER role).
     *
     * @param model contains the group ID, optional expiry hours, optional max-uses,
     *              and the caller's username
     * @return the newly created share link with its invite code
     */
    ShareLinkResponse createShareLink(CreateShareLinkModel model);

    /**
     * Returns all share links that have been created for a group.
     *
     * @param model contains the group ID and the caller's username (must be a
     *              member)
     * @return list of share link records for the group
     */
    List<ShareLinkResponse> getGroupShareLinks(GetGroupShareLinksModel model);

    /**
     * Revokes (deactivates) a specific share link (requires OWNER role).
     *
     * @param model contains the group ID, link ID, and the caller's username
     * @return the updated share link with REVOKED status
     */
    ShareLinkResponse revokeShareLink(RevokeShareLinkModel model);

    /**
     * Returns a preview of the group associated with a share invite code (public
     * endpoint).
     * <p>
     * Used to display group info before the user decides to join.
     *
     * @param model contains the invite code string
     * @return group preview (name, member count, validity status); never throws —
     *         returns invalid state instead
     */
    GroupPreviewResponse getGroupPreview(GetGroupPreviewModel model);

    /**
     * Joins a group via a valid share invite code (authenticated users only).
     *
     * @param model contains the invite code and the caller's username
     * @return the share link record with updated usage count; throws on expired or
     *         revoked links
     */
    ShareLinkResponse joinGroupViaLink(JoinViaLinkModel model);
}
