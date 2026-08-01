package com.hcmut.divvy.service.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendInvitationModel {
    private Integer groupId;
    private Integer inviteeId;
    private String usernameOrEmail;
    private String message;
    private LocalDateTime expiresAt;
    private String currentUsername;
}
