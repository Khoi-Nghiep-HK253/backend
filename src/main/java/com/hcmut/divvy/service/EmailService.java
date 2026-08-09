package com.hcmut.divvy.service;

public interface EmailService {

    /**
     * Send a password reset email containing a link with the given token.
     *
     * @param toEmail   recipient email address
     * @param resetLink full reset URL, e.g.
     *                  https://app.divvy.com/reset-password?token=xxx
     */
    void sendPasswordResetEmail(String toEmail, String resetLink);

    /**
     * Send a group invitation email to the invitee.
     *
     * @param toEmail     recipient email address
     * @param inviterName name or username of the inviter
     * @param groupName   name of the expense group
     * @param inviteLink  full invitation acceptance URL
     * @param message     optional personal invitation message
     */
    void sendGroupInvitationEmail(String toEmail, String inviterName, String groupName, String inviteLink,
            String message);
}
