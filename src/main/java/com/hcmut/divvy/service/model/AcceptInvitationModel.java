package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptInvitationModel {
    private Integer invitationId;
    private String currentUsername;
}
