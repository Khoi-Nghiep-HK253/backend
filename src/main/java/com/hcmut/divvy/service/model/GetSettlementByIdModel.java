package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSettlementByIdModel {
    private String currentUsername;
    private Integer groupId;
    private Integer settlementId;
}
