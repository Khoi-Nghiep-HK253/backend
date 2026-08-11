package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.response.AnalyticsSummaryResponse;
import com.hcmut.divvy.mapper.AnalyticsMapper;
import com.hcmut.divvy.service.AnalyticsService;
import com.hcmut.divvy.service.model.GetAnalyticsModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for Dashboard and Financial Analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsMapper analyticsMapper;

    @GetMapping("/summary")
    @Operation(summary = "Get financial analytics summary, category breakdowns, and spending trends")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getAnalyticsSummary(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "DAY") String groupBy,
            Authentication authentication) {

        GetAnalyticsModel model = analyticsMapper.toGetAnalyticsModel(
                authentication.getName(), groupId, startDate, endDate, groupBy);

        AnalyticsSummaryResponse summary = analyticsService.getAnalyticsSummary(model);
        return ResponseEntity.ok(ApiResponse.ok(summary, "Analytics summary fetched successfully"));
    }
}
