package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.InvitationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptInvitationResponse {

    private Integer invitationId;
    private InvitationStatus status;
    private JoinedGroupInfo joinedGroup;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JoinedGroupInfo {
        private Integer id;
        private String name;
    }
}
