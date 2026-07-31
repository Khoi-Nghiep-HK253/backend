package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberModel {
    private Integer groupId;
    private Integer userId;
    private String currentUsername;
}
