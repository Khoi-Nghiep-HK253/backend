package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.ExpenseResponse;
import com.hcmut.divvy.dto.response.ExpenseSummaryResponse;
import com.hcmut.divvy.service.model.*;
import org.springframework.data.domain.Page;

public interface ExpenseService {
    ExpenseResponse create(CreateExpenseModel model);
    Page<ExpenseSummaryResponse> getGroupExpenses(GetGroupExpensesModel model);
    ExpenseResponse findById(GetExpenseByIdModel model);
    ExpenseResponse update(UpdateExpenseModel model);
    void delete(DeleteExpenseModel model);
}
