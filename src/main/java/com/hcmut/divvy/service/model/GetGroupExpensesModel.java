package com.hcmut.divvy.service.model;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetGroupExpensesModel {
    private String currentUsername;
    private Integer groupId;
    private Integer categoryId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Pageable pageable;
}
