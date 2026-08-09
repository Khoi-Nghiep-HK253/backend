package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.ExpenseResponse;
import com.hcmut.divvy.dto.response.ExpenseSummaryResponse;
import com.hcmut.divvy.service.model.*;
import org.springframework.data.domain.Page;

public interface ExpenseService {

    /**
     * Creates a new expense in a group, calculates splits, and generates debt
     * records.
     *
     * @param model expense details including amount, split type, payers, and shares
     * @return the newly created expense with full split breakdown
     */
    ExpenseResponse create(CreateExpenseModel model);

    /**
     * Returns a paginated list of expenses in a group, optionally filtered by date
     * or user.
     *
     * @param model contains the group ID, pagination settings, and optional filters
     * @return page of expense summaries
     */
    Page<ExpenseSummaryResponse> getGroupExpenses(GetGroupExpensesModel model);

    /**
     * Returns the full detail of a single expense by its ID.
     *
     * @param model contains the expense ID and the caller's username (for access
     *              check)
     * @return detailed expense information; throws 404 if not found
     */
    ExpenseResponse findById(GetExpenseByIdModel model);

    /**
     * Updates an existing expense and recalculates all splits and debt records.
     *
     * @param model updated expense fields (amount, description, split type, payers,
     *              shares)
     * @return the updated expense with recalculated splits
     */
    ExpenseResponse update(UpdateExpenseModel model);

    /**
     * Permanently deletes an expense and all associated debt records.
     *
     * @param model contains the expense ID and the caller's username (must be
     *              creator or OWNER)
     */
    void delete(DeleteExpenseModel model);
}
