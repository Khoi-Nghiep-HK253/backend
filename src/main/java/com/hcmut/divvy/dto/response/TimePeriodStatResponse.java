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
public class TimePeriodStatResponse {
    private String periodLabel;
    private BigDecimal totalAmount;
}
