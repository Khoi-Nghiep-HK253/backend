package com.hcmut.divvy.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserModel {
    private Integer id;
    private String firstname;
    private String lastname;
    private String phone;
    private String currentUsername;
}
