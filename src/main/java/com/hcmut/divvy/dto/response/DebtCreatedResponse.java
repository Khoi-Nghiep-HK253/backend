package com.hcmut.divvy.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtCreatedResponse {
    private Integer id;
    private Integer fromUserId;
    private String fromUsername;
    private Integer toUserId;
    private String toUsername;
    private BigDecimal amount;
    private String status;
}
