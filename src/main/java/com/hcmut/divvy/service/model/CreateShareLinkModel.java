package com.hcmut.divvy.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShareLinkModel {
    private Integer groupId;
    private String currentUsername;
    private Integer expireHours;
    private Integer maxUses;
}
