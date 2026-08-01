package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.SettlementDetailResponse;
import com.hcmut.divvy.dto.response.SettlementResponse;
import com.hcmut.divvy.dto.response.SettlementSummaryResponse;
import com.hcmut.divvy.service.model.CreateSettlementModel;
import com.hcmut.divvy.service.model.GetGroupSettlementsModel;
import com.hcmut.divvy.service.model.GetSettlementByIdModel;
import org.springframework.data.domain.Page;

public interface SettlementService {
    SettlementResponse create(CreateSettlementModel model);
    Page<SettlementSummaryResponse> getGroupSettlements(GetGroupSettlementsModel model);
    SettlementDetailResponse findById(GetSettlementByIdModel model);
}
