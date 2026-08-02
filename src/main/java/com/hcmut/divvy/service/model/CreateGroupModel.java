package com.hcmut.divvy.service.model;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupModel {
    private String name;
    private String description;
    private String note;
    private Integer categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currentUsername;
}
