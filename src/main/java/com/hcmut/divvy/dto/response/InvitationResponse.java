package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.InvitationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationResponse {

    private Integer id;
    private GroupInfo group;
    private UserInfo inviter;
    private UserInfo invitee;
    private InvitationStatus status;
    private String token;
    private String message;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GroupInfo {
        private Integer id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserInfo {
        private Integer id;
        private String username;
        private String firstname;
        private String lastname;
    }
}
