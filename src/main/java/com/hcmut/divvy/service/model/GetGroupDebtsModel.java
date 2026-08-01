package com.hcmut.divvy.service.model;

import com.hcmut.divvy.entity.enums.DebtStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupDebtsModel {
    private String currentUsername;
    private Integer groupId;
    private DebtStatus status;
    private Integer userId;
}
