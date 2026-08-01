package com.hcmut.divvy.service.model;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupSettlementsModel {
    private String currentUsername;
    private Integer groupId;
    private Integer fromUserId;
    private Integer toUserId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Pageable pageable;
}
