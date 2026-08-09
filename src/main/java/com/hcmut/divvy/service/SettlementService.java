package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.SettlementDetailResponse;
import com.hcmut.divvy.dto.response.SettlementResponse;
import com.hcmut.divvy.dto.response.SettlementSummaryResponse;
import com.hcmut.divvy.service.model.CreateSettlementModel;
import com.hcmut.divvy.service.model.GetGroupSettlementsModel;
import com.hcmut.divvy.service.model.GetSettlementByIdModel;
import org.springframework.data.domain.Page;

public interface SettlementService {

    /**
     * Records a new debt settlement (payment) between two members of a group.
     *
     * @param model settlement details (amount, method, note, payer, payee) and the
     *              caller's username
     * @return the newly created settlement record
     */
    SettlementResponse create(CreateSettlementModel model);

    /**
     * Returns a paginated list of settlements within a group, optionally filtered
     * by date or user.
     *
     * @param model contains the group ID, pagination settings, and optional filters
     * @return page of settlement summaries
     */
    Page<SettlementSummaryResponse> getGroupSettlements(GetGroupSettlementsModel model);

    /**
     * Returns the full detail of a single settlement record by its ID.
     *
     * @param model contains the settlement ID and the caller's username (for access
     *              check)
     * @return detailed settlement information; throws 404 if not found
     */
    SettlementDetailResponse findById(GetSettlementByIdModel model);
}
