package com.hcmut.divvy.service.model;

import com.hcmut.divvy.dto.request.ExpensePayerRequest;
import com.hcmut.divvy.dto.request.ExpenseShareRequest;
import com.hcmut.divvy.entity.enums.SplitType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseModel {
    private String currentUsername;
    private Integer groupId;
    private String description;
    private BigDecimal totalAmount;
    private Integer currencyId;
    private Integer categoryId;
    private LocalDate expenseDate;
    private SplitType splitType;
    private List<ExpensePayerRequest> payers;
    private List<ExpenseShareRequest> shares;
}
