package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveMemberModel {
    private Integer groupId;
    private Integer memberId;
    private String currentUsername;
}
