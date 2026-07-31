package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordModel {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
