package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordModel {
    private Integer id;
    private String oldPassword;
    private String newPassword;
    private String currentUsername;
}
