package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMyDebtsModel {
    private String currentUsername;
    private Integer groupId;
}
