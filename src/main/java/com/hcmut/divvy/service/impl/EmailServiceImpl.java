package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@divvy.app}")
    private String fromAddress;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        if (!mailEnabled) {
            // Dev fallback: log the reset link instead of sending a real email
            log.warn("========================================================");
            log.warn("[DEV MODE] Password reset link for {}:", toEmail);
            log.warn("  {}", resetLink);
            log.warn("========================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("[Divvy] Reset your password");
            helper.setText(buildEmailBody(resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            // Don't rethrow — email failure should not crash the request
        }
    }

    private String buildEmailBody(String resetLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                    <h2 style="color: #4f46e5;">🔐 Divvy — Reset your password</h2>
                    <p>You requested to reset your password. Click the button below to set a new one.</p>
                    <p style="text-align: center; margin: 32px 0;">
                        <a href="%s"
                           style="background-color: #4f46e5; color: white; padding: 12px 28px;
                                  border-radius: 6px; text-decoration: none; font-weight: bold;">
                            Reset Password
                        </a>
                    </p>
                    <p style="color: #666; font-size: 13px;">This link will expire in <strong>30 minutes</strong>.</p>
                    <p style="color: #666; font-size: 13px;">If you did not request this, you can safely ignore this email.</p>
                    <hr style="margin-top: 32px; border: none; border-top: 1px solid #e0e0e0;"/>
                    <p style="color: #aaa; font-size: 12px;">© Divvy App</p>
                </div>
                """.formatted(resetLink);
    }
}
