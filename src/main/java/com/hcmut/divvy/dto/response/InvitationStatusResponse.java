package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.InvitationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationStatusResponse {

    private Integer invitationId;
    private InvitationStatus status;
}
