package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface DebtService {
    List<DebtItemResponse> getGroupDebts(GetGroupDebtsModel model);
    DebtGroupSummaryResponse getGroupDebtSummary(GetGroupDebtSummaryModel model);
    MyDebtsResponse getMyDebts(GetMyDebtsModel model);
    DebtDetailResponse getDebtById(GetDebtByIdModel model);
}
