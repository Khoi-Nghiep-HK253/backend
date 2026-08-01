package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseShareRequest {

    @NotNull(message = "Share participant userId is required")
    private Integer userId;

    /**
     * Exact amount for EXACT split mode or calculated amount.
     */
    private BigDecimal amount;

    /**
     * Percentage for PERCENTAGE split mode (0.0 to 100.0).
     */
    private BigDecimal percentage;

    /**
     * Ratio / number of shares for SHARES split mode.
     */
    private BigDecimal ratio;

    /**
     * Additional adjustment amount for ADJUSTMENT split mode (+ or -).
     */
    private BigDecimal adjustment;
}
