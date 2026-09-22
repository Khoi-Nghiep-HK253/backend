package com.hcmut.divvy.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestGroupCategoryModel {
    private String name;
    private String note;
}
