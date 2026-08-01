package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpensePayerRequest {

    @NotNull(message = "Payer userId is required")
    private Integer userId;

    @NotNull(message = "Payer amount is required")
    @DecimalMin(value = "0.01", message = "Payer amount must be greater than 0")
    private BigDecimal amount;
}
