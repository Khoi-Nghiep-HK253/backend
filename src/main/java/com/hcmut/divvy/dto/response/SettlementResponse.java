package com.hcmut.divvy.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {
    private Integer id;
    private DebtStatusInfo debt;
    private DebtUserInfoResponse fromUser;
    private DebtUserInfoResponse toUser;
    private BigDecimal amount;
    private String method;
    private String note;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DebtStatusInfo {
        private Integer id;
        private String newStatus;
    }
}
