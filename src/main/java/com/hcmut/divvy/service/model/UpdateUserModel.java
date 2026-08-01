package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserModel {
    private Integer id;
    private String firstname;
    private String lastname;
    private String phone;
    private String currentUsername;
}
