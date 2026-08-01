package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberModel {
    private Integer groupId;
    private Integer userId;
    private String currentUsername;
}
