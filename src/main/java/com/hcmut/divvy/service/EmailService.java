package com.hcmut.divvy.service;

public interface EmailService {

    /**
     * Send a password reset email containing a link with the given token.
     *
     * @param toEmail   recipient email address
     * @param resetLink full reset URL, e.g. https://app.divvy.com/reset-password?token=xxx
     */
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
