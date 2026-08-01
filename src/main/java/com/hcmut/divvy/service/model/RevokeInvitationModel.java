package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokeInvitationModel {
    private Integer invitationId;
    private String currentUsername;
}
