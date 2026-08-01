package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteExpenseModel {
    private String currentUsername;
    private Integer groupId;
    private Integer expenseId;
}
