package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeInvitationModel {
    private Integer invitationId;
    private String currentUsername;
}
