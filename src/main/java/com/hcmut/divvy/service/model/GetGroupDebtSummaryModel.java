package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetGroupDebtSummaryModel {
    private String currentUsername;
    private Integer groupId;
}
