package com.hcmut.divvy.service.model;

import com.hcmut.divvy.entity.enums.InvitationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMyInvitationsModel {
    private InvitationStatus status;
    private String currentUsername;
}
