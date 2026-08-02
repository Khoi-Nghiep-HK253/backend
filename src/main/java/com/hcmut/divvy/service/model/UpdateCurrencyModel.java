package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCurrencyModel {
    private Integer id;
    private String name;
    private String acronym;
}
