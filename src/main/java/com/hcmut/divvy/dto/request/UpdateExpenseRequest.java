package com.hcmut.divvy.dto.request;

import com.hcmut.divvy.entity.enums.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateExpenseRequest {

    @NotBlank(message = "Expense description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotNull(message = "Currency ID is required")
    private Integer currencyId;

    private Integer categoryId;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    @Builder.Default
    private SplitType splitType = SplitType.EQUAL;

    @NotEmpty(message = "Payers list cannot be empty")
    @Valid
    private List<ExpensePayerRequest> payers;

    @NotEmpty(message = "Shares list cannot be empty")
    @Valid
    private List<ExpenseShareRequest> shares;
}
