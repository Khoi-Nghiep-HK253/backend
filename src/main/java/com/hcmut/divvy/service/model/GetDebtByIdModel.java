package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDebtByIdModel {
    private String currentUsername;
    private Integer groupId;
    private Integer debtId;
}
