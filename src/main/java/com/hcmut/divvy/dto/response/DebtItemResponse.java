package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.DebtStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtItemResponse {
    private Integer id;
    private ExpenseInfo expense;
    private DebtUserInfoResponse fromUser;
    private DebtUserInfoResponse toUser;
    private BigDecimal amount;
    private DebtStatus status;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpenseInfo {
        private Integer id;
        private String description;
    }
}
