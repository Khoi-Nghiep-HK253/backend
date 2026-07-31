package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordModel {
    private Integer id;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String currentUsername;
}
