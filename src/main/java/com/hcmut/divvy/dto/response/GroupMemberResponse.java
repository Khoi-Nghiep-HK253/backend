package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.GroupRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponse {
    private Integer id;
    private UserInfo user;
    private GroupRole role;
    private LocalDateTime joinedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserInfo {
        private Integer id;
        private String username;
        private String firstname;
        private String lastname;
    }
}
