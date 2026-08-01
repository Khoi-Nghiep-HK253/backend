package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendInvitationRequest {

    @NotNull(message = "inviteeId is required")
    private Integer inviteeId;

    private String message;

    private LocalDateTime expiresAt;
}
