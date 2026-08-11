package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryResponse {
    private BigDecimal totalPersonalShare;
    private BigDecimal totalGroupExpense;
    private BigDecimal totalOwedToUser;
    private BigDecimal totalUserOwes;
    private List<CategoryExpenseStatResponse> categoryStats;
    private List<TimePeriodStatResponse> timeTrendStats;
    private List<ExpenseResponse> topExpenses;
}
