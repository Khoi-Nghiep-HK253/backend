package com.hcmut.divvy.service.model;

import com.hcmut.divvy.entity.enums.InvitationStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMyInvitationsModel {
    private InvitationStatus status;
    private String currentUsername;
}
