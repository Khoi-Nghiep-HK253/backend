package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@divvy.app}")
    private String fromAddress;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("classpath:templates/email/password-reset.html")
    private Resource passwordResetTemplateResource;

    @Value("classpath:templates/email/group-invitation.html")
    private Resource invitationTemplateResource;

    @Value("classpath:templates/email/personal-message.html")
    private Resource personalMessageTemplateResource;

    @Value("classpath:templates/email/welcome.html")
    private Resource welcomeTemplateResource;

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        if (!mailEnabled) {
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
            helper.setText(buildResetEmailBody(resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendGroupInvitationEmail(String toEmail, String inviterName, String groupName, String inviteLink,
            String personalMessage) {
        if (!mailEnabled) {
            log.warn("========================================================");
            log.warn("[DEV MODE] Group invitation email for {}:", toEmail);
            log.warn("  Inviter: {}, Group: {}", inviterName, groupName);
            log.warn("  Link: {}", inviteLink);
            log.warn("========================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("[Divvy] " + inviterName + " đã mời bạn tham gia nhóm \"" + groupName + "\"");
            helper.setText(buildInvitationEmailBody(inviterName, groupName, inviteLink, personalMessage), true);

            mailSender.send(message);
            log.info("Group invitation email sent to {} for group '{}'", toEmail, groupName);

        } catch (MessagingException e) {
            log.error("Failed to send group invitation email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildResetEmailBody(String resetLink) {
        try {
            String template = passwordResetTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template.formatted(resetLink);
        } catch (IOException e) {
            log.error("Failed to read password reset email template", e);
            throw new RuntimeException("Could not load email template", e);
        }
    }

    private String buildInvitationEmailBody(String inviterName, String groupName, String inviteLink,
            String personalMessage) {
        try {
            String messageSection = "";
            if (personalMessage != null && !personalMessage.isBlank()) {
                String messageTemplate = personalMessageTemplateResource.getContentAsString(StandardCharsets.UTF_8);
                messageSection = messageTemplate.formatted(personalMessage);
            }

            String template = invitationTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template.formatted(inviterName, groupName, messageSection, inviteLink, inviteLink);
        } catch (IOException e) {
            log.error("Failed to read group invitation email template", e);
            throw new RuntimeException("Could not load email template", e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String username, String welcomeLink) {
        if (!mailEnabled) {
            log.warn("========================================================");
            log.warn("[DEV MODE] Welcome email for {}:", toEmail);
            log.warn("  Username: {}", username);
            log.warn("  Welcome Link: {}", welcomeLink);
            log.warn("========================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("[Divvy] Welcome to Divvy!");
            helper.setText(buildWelcomeEmailBody(username, welcomeLink), true);

            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildWelcomeEmailBody(String username, String welcomeLink) {
        try {
            String template = welcomeTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template.replace("{{username}}", username != null ? username : "").replace("{{welcomeLink}}", welcomeLink != null ? welcomeLink : "");
        } catch (IOException e) {
            log.error("Failed to read welcome email template", e);
            throw new RuntimeException("Could not load email template", e);
        }
    }
}
