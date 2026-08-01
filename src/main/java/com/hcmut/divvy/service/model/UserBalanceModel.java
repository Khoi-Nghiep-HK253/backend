package com.hcmut.divvy.service.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBalanceModel {
    private Integer userId;
    private BigDecimal amount;

    public void subtractAmount(BigDecimal value) {
        if (value != null && amount != null) {
            this.amount = this.amount.subtract(value);
        }
    }
}
