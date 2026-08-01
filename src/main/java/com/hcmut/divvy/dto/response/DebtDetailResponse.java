package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.DebtStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtDetailResponse {
    private Integer id;
    private ExpenseDetailInfo expense;
    private DebtUserInfoResponse fromUser;
    private DebtUserInfoResponse toUser;
    private BigDecimal amount;
    private DebtStatus status;
    private List<Object> settlements;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpenseDetailInfo {
        private Integer id;
        private String description;
        private LocalDate expenseDate;
    }
}
