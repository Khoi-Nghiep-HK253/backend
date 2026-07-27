package com.hcmut.divvy.entity.enums;

/**
 * Represents the lifecycle status of a group invitation.
 */
public enum InvitationStatus {

    /** Invitation has been sent but not yet responded to. */
    PENDING,

    /** Invitee accepted the invitation and joined the group. */
    ACCEPTED,

    /** Invitee declined the invitation. */
    DECLINED,

    /** Invitation expired before the invitee responded. */
    EXPIRED,

    /** Invitation was revoked by the inviter before being responded to. */
    REVOKED
}
