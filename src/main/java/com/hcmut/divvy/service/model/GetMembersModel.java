package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMembersModel {
    private Integer groupId;
    private String currentUsername;
}
