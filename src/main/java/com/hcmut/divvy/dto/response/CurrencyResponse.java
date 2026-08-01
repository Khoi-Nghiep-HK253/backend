package com.hcmut.divvy.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyResponse {
    private Integer id;
    private String name;
    private String code;
}
