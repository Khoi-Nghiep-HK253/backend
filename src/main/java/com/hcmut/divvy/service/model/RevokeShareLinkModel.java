package com.hcmut.divvy.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeShareLinkModel {
    private Integer groupId;
    private Integer linkId;
    private String currentUsername;
}
