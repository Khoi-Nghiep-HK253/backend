package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteGroupModel {
    private Integer groupId;
    private String currentUsername;
}
