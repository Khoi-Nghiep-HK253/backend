package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDebtByIdModel {
    private String currentUsername;
    private Integer groupId;
    private Integer debtId;
}
