package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRoleModel {
    private Integer groupId;
    private Integer memberId;
    private String role;
    private String currentUsername;
}
