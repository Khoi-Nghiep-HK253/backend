package com.hcmut.divvy.service.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGroupModel {
    private Integer groupId;
    private String name;
    private Integer categoryId;
    private Integer defaultCurrencyId;
    private String note;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currentUsername;
}
