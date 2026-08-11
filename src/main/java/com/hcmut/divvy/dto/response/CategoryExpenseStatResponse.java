package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpenseStatResponse {
    private Integer categoryId;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal totalAmount;
    private Double percentage;
    private Long expenseCount;
}
