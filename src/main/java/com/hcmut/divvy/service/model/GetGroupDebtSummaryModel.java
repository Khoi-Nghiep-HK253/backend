package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupDebtSummaryModel {
    private String currentUsername;
    private Integer groupId;
}
