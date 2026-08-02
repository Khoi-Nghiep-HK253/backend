package com.hcmut.divvy.service.model;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupExpensesModel {
    private Integer groupId;
    private String currentUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Pageable pageable;
}
