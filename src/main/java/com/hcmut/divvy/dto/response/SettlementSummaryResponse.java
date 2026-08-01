package com.hcmut.divvy.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementSummaryResponse {
    private Integer id;
    private DebtUserInfoResponse fromUser;
    private DebtUserInfoResponse toUser;
    private BigDecimal amount;
    private String method;
    private LocalDateTime paidAt;
}
