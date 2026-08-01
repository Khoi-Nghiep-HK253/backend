package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateExpenseRequest;
import com.hcmut.divvy.dto.request.UpdateExpenseRequest;
import com.hcmut.divvy.dto.response.ExpenseResponse;
import com.hcmut.divvy.dto.response.ExpenseSummaryResponse;
import com.hcmut.divvy.mapper.ExpenseMapper;
import com.hcmut.divvy.service.ExpenseService;
import com.hcmut.divvy.service.model.CreateExpenseModel;
import com.hcmut.divvy.service.model.GetGroupExpensesModel;
import com.hcmut.divvy.service.model.UpdateExpenseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
@Tag(name = "Expense", description = "Group expense management APIs")
@PreAuthorize("isAuthenticated()")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @PostMapping
    @Operation(summary = "Create a new expense", description = "Creates a new expense and automatically calculates debts between payers and participants")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication
    ) {
        CreateExpenseModel model = expenseMapper.toModel(request, groupId, authentication.getName());
        ExpenseResponse response = expenseService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Expense created successfully"));
    }

    @GetMapping
    @Operation(summary = "List group expenses", description = "Gets a paginated list of expenses for the group with optional category and date range filters")
    public ResponseEntity<ApiResponse<Page<ExpenseSummaryResponse>>> getGroupExpenses(
            @PathVariable Integer groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate", "createdAt"));
        GetGroupExpensesModel model = expenseMapper.toGetGroupExpensesModel(
                groupId, authentication.getName(), categoryId, fromDate, toDate, pageable);

        Page<ExpenseSummaryResponse> pageResult = expenseService.getGroupExpenses(model);
        return ResponseEntity.ok(ApiResponse.ok(pageResult, "Expenses retrieved successfully"));
    }

    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense detail", description = "Retrieves full details of a specific expense including payers, shares, and debts")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable Integer groupId,
            @PathVariable Integer expenseId,
            Authentication authentication
    ) {
        ExpenseResponse response = expenseService.findById(
                expenseMapper.toGetExpenseByIdModel(groupId, expenseId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(response, "Expense retrieved successfully"));
    }

    @PutMapping("/{expenseId}")
    @Operation(summary = "Update an expense", description = "Updates an expense and recalculates all associated shares and debts")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Integer groupId,
            @PathVariable Integer expenseId,
            @Valid @RequestBody UpdateExpenseRequest request,
            Authentication authentication
    ) {
        UpdateExpenseModel model = expenseMapper.toModel(request, groupId, expenseId, authentication.getName());
        ExpenseResponse response = expenseService.update(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Expense updated successfully"));
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete an expense", description = "Deletes an expense along with all associated payers, shares, and pending debts")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Integer groupId,
            @PathVariable Integer expenseId,
            Authentication authentication
    ) {
        expenseService.delete(expenseMapper.toDeleteExpenseModel(groupId, expenseId, authentication.getName()));
        return ResponseEntity.ok(ApiResponse.ok(null, "Expense deleted successfully"));
    }
}
