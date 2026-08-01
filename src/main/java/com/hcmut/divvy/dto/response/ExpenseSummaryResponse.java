package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.SplitType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryResponse {
    private Integer id;
    private String description;
    private BigDecimal totalAmount;
    private CurrencyResponse currency;
    private CategoryResponse category;
    private LocalDate expenseDate;
    private SplitType splitType;
    private Integer payerCount;
    private Integer shareCount;
}
