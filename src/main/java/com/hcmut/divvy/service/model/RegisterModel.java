package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterModel {
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String password;
}
