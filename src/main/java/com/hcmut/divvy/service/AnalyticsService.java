package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.AnalyticsSummaryResponse;
import com.hcmut.divvy.service.model.GetAnalyticsModel;

public interface AnalyticsService {
    AnalyticsSummaryResponse getAnalyticsSummary(GetAnalyticsModel model);
}
