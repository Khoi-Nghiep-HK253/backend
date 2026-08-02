package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryModel {
    private Integer id;
    private String name;
    private String icon;
}
