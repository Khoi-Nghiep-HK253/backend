package com.hcmut.divvy.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinViaLinkModel {
    private String inviteCode;
    private String currentUsername;
}
