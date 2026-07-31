package com.hcmut.divvy.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String firstname;

    private String lastname;

    private String phone;
}
