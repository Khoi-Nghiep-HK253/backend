package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSettlementByIdModel {
    private String currentUsername;
    private Integer groupId;
    private Integer settlementId;
}
