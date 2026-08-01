package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSettlementRequest {

    @NotNull(message = "Debt ID is required")
    private Integer debtId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Builder.Default
    private String method = "CASH";

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    private LocalDateTime paidAt;
}
