package com.hcmut.divvy.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.divvy.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${app.mail.resend-api-url:https://api.resend.com/emails}")
    private String resendApiUrl;

    @Value("${app.mail.resend-api-key:}")
    private String resendApiKey;

    @Value("${app.mail.from:onboarding@resend.dev}")
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

    private void sendResendEmail(String toEmail, String subject, String htmlContent) {
        if (!mailEnabled) {
            log.warn("========================================================");
            log.warn("[DEV MODE] Email disabled. Would send email to {}:", toEmail);
            log.warn("  Subject: {}", subject);
            log.warn("========================================================");
            return;
        }

        if (resendApiKey == null || resendApiKey.isBlank()) {
            log.error("[RESEND EMAIL] RESEND_API_KEY is missing! Skipping email send to {}", toEmail);
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromAddress);
            payload.put("to", List.of(toEmail));
            payload.put("subject", subject);
            payload.put("html", htmlContent);

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(resendApiUrl.trim()))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email successfully sent via Resend API to {}. Response: {}", toEmail, response.body());
            } else {
                log.error("Failed to send email via Resend API to {}. Status: {}, Response: {}",
                        toEmail, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Error sending email via Resend API to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "[Divvy] Reset your password";
        String htmlContent = buildResetEmailBody(resetLink);
        sendResendEmail(toEmail, subject, htmlContent);
    }

    @Override
    @Async
    public void sendGroupInvitationEmail(String toEmail, String inviterName, String groupName, String inviteLink,
            String personalMessage) {
        String subject = "[Divvy] " + inviterName + " invited you to join group \"" + groupName + "\"";
        String htmlContent = buildInvitationEmailBody(inviterName, groupName, inviteLink, personalMessage);
        sendResendEmail(toEmail, subject, htmlContent);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String username, String welcomeLink) {
        String subject = "[Divvy] Welcome to Divvy!";
        String htmlContent = buildWelcomeEmailBody(username, welcomeLink);
        sendResendEmail(toEmail, subject, htmlContent);
    }

    private String buildResetEmailBody(String resetLink) {
        try {
            String template = passwordResetTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template.replace("{{resetLink}}", resetLink != null ? resetLink : "");
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
                messageSection = messageTemplate.replace("{{personalMessage}}", personalMessage);
            }

            String template = invitationTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template
                    .replace("{{inviterName}}", inviterName != null ? inviterName : "")
                    .replace("{{groupName}}", groupName != null ? groupName : "")
                    .replace("{{messageSection}}", messageSection)
                    .replace("{{inviteLink}}", inviteLink != null ? inviteLink : "");
        } catch (IOException e) {
            log.error("Failed to read group invitation email template", e);
            throw new RuntimeException("Could not load email template", e);
        }
    }

    private String buildWelcomeEmailBody(String username, String welcomeLink) {
        try {
            String template = welcomeTemplateResource.getContentAsString(StandardCharsets.UTF_8);
            return template
                    .replace("{{username}}", username != null ? username : "")
                    .replace("{{welcomeLink}}", welcomeLink != null ? welcomeLink : "");
        } catch (IOException e) {
            log.error("Failed to read welcome email template", e);
            throw new RuntimeException("Could not load email template", e);
        }
    }
}