package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMyDebtsModel {
    private String currentUsername;
    private Integer groupId;
}
