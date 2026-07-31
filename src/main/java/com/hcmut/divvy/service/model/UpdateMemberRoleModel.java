package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRoleModel {
    private Integer groupId;
    private Integer memberId;
    private String role;
    private String currentUsername;
}
