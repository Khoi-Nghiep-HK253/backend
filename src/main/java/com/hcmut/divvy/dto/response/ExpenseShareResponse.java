package com.hcmut.divvy.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseShareResponse {
    private Integer userId;
    private String username;
    private BigDecimal amount;
}
