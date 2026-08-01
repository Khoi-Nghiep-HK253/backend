package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterModel {
    private String username;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String password;
}
