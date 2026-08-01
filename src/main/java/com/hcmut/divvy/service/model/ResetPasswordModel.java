package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordModel {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
