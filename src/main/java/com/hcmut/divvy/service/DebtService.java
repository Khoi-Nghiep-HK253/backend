package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface DebtService {

    /**
     * Returns the list of unsettled debts within a group, optionally filtered by
     * user.
     *
     * @param model contains the group ID, optional filters, and the caller's
     *              username
     * @return list of debt items (who owes whom and how much)
     */
    List<DebtItemResponse> getGroupDebts(GetGroupDebtsModel model);

    /**
     * Returns a high-level summary of all debts in a group (total owed per member).
     *
     * @param model contains the group ID and the caller's username
     * @return aggregated debt summary for the group
     */
    DebtGroupSummaryResponse getGroupDebtSummary(GetGroupDebtSummaryModel model);

    /**
     * Returns all debts that the currently authenticated user is involved in
     * (both as debtor and creditor) across all their groups.
     *
     * @param model contains the caller's username
     * @return a combined view of debts owed to and by the current user
     */
    MyDebtsResponse getMyDebts(GetMyDebtsModel model);

    /**
     * Returns the full detail of a single debt record by its ID.
     *
     * @param model contains the debt ID and the caller's username (for access
     *              check)
     * @return detailed debt information; throws 404 if not found
     */
    DebtDetailResponse getDebtById(GetDebtByIdModel model);
}
