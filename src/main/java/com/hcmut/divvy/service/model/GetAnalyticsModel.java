package com.hcmut.divvy.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAnalyticsModel {
    private String currentUsername;
    private Integer groupId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String groupBy;
}
