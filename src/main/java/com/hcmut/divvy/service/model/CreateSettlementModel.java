package com.hcmut.divvy.service.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSettlementModel {
    private String currentUsername;
    private Integer groupId;
    private Integer debtId;
    private BigDecimal amount;
    private String method;
    private String note;
    private LocalDateTime paidAt;
}
