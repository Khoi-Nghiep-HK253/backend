package com.hcmut.divvy.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyDebtsResponse {
    private List<IOweGroupResponse> iOwe;
    private List<OwedToMeGroupResponse> owedToMe;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IOweGroupResponse {
        private DebtUserInfoResponse toUser;
        private BigDecimal totalAmount;
        private List<DebtSubItemResponse> debts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwedToMeGroupResponse {
        private DebtUserInfoResponse fromUser;
        private BigDecimal totalAmount;
        private List<DebtSubItemResponse> debts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DebtSubItemResponse {
        private Integer id;
        private BigDecimal amount;
        private Integer expenseId;
    }
}
