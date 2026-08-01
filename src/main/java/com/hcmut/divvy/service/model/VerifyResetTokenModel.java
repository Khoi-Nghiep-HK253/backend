package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResetTokenModel {
    private String token;
}
