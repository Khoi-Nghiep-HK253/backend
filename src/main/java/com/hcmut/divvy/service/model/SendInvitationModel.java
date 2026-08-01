package com.hcmut.divvy.service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendInvitationModel {
    private Integer groupId;
    private Integer inviteeId;
    private String message;
    private LocalDateTime expiresAt;
    private String currentUsername;
}
