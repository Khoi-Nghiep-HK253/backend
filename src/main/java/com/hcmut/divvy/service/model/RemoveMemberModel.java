package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveMemberModel {
    private Integer groupId;
    private Integer memberId;
    private String currentUsername;
}
