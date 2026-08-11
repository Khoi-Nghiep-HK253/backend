package com.hcmut.divvy.mapper;

import com.hcmut.divvy.service.model.GetAnalyticsModel;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AnalyticsMapper {

    default GetAnalyticsModel toGetAnalyticsModel(
            String currentUsername,
            Integer groupId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String groupBy) {
        return GetAnalyticsModel.builder()
                .currentUsername(currentUsername)
                .groupId(groupId)
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy)
                .build();
    }
}
