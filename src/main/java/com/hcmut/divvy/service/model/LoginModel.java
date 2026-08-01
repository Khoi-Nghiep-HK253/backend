package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginModel {
    private String usernameOrEmail;
    private String password;
}
