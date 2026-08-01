package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteExpenseModel {
    private Integer groupId;
    private Integer expenseId;
    private String currentUsername;
}
