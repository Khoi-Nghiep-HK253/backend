package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.SplitType;
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
public class ExpenseResponse {
    private Integer id;
    private GroupResponse group;
    private String description;
    private BigDecimal totalAmount;
    private CurrencyResponse currency;
    private CategoryResponse category;
    private LocalDate expenseDate;
    private SplitType splitType;
    private List<ExpensePayerResponse> payers;
    private List<ExpenseShareResponse> shares;
    private List<DebtCreatedResponse> debtsCreated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
