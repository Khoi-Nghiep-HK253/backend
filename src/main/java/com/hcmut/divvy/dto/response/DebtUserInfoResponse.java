package com.hcmut.divvy.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtUserInfoResponse {
    private Integer id;
    private String username;
    private String fullname;
}
