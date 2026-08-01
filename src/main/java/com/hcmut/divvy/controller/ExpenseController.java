package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateExpenseRequest;
import com.hcmut.divvy.dto.request.UpdateExpenseRequest;
import com.hcmut.divvy.dto.response.ExpenseResponse;
import com.hcmut.divvy.dto.response.ExpenseSummaryResponse;
import com.hcmut.divvy.mapper.ExpenseMapper;
import com.hcmut.divvy.service.ExpenseService;
import com.hcmut.divvy.service.model.*;
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

    /**
     * Create a new expense in a group.
     * <p>
     * After persisting the expense, the system automatically:
     * <ol>
     *   <li>Saves the list of payers ({@code ExpensePayer}).</li>
     *   <li>Calculates the share amounts according to {@code splitType} and saves {@code ExpenseShare}.</li>
     *   <li>Computes net balances and saves {@code Debt} records using a Two-Pointer Greedy algorithm.</li>
     * </ol>
     * Supported split types: {@code EQUAL}, {@code EXACT}, {@code PERCENTAGE}, {@code SHARES}, {@code ADJUSTMENT}.
     *
     * @param groupId        the group's ID
     * @param request        expense payload (description, totalAmount, currencyId, splitType, payers, shares, ...)
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 201 Created} with the full ExpenseResponse (expense + payers + shares + debts);
     *         {@code 403} if the caller is not a member; {@code 400} if payer or share data is invalid
     */
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

    /**
     * Retrieve a paginated list of expenses for a group, with optional filters.
     * <p>
     * Results are sorted by {@code expenseDate} and {@code createdAt} descending.
     * Optional filters: category and date range ({@code fromDate} to {@code toDate}).
     *
     * @param groupId        the group's ID
     * @param page           page number (zero-based, default 0)
     * @param size           page size (default 20)
     * @param categoryId     optional category filter
     * @param fromDate       optional lower bound date (ISO: yyyy-MM-dd)
     * @param toDate         optional upper bound date (ISO: yyyy-MM-dd)
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with {@code Page<ExpenseSummaryResponse>};
     *         {@code 403} if the caller is not a member
     */
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

    /**
     * Retrieve the full details of a specific expense.
     * <p>
     * Includes the list of payers, participant shares, and debts generated by this expense.
     *
     * @param groupId        the group's ID
     * @param expenseId      the expense's ID
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with the full ExpenseResponse;
     *         {@code 404} if the expense does not exist; {@code 403} if the caller is not a member
     */
    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense detail", description = "Retrieves full details of a specific expense including payers, shares, and debts")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable Integer groupId,
            @PathVariable Integer expenseId,
            Authentication authentication
    ) {
        GetExpenseByIdModel model = expenseMapper.toGetExpenseByIdModel(groupId, expenseId, authentication.getName());
        ExpenseResponse response = expenseService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Expense retrieved successfully"));
    }

    /**
     * Update an existing expense and recalculate all associated shares and debts.
     * <p>
     * Authorized callers: the expense creator or an {@code OWNER} of the group.
     * Cannot update an expense if any of its debts have already been settled ({@code SETTLED}).
     * On success, all existing Payers, Shares, and Debts are deleted and recreated from scratch.
     *
     * @param groupId        the group's ID
     * @param expenseId      the expense's ID
     * @param request        the update payload
     * @param authentication the currently authenticated user
     * @return {@code 200 OK} with the updated ExpenseResponse;
     *         {@code 400} if any debt is already SETTLED;
     *         {@code 403} if the caller is neither the creator nor an OWNER
     */
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

    /**
     * Delete an expense together with all of its associated data.
     * <p>
     * Authorized callers: the expense creator or an {@code OWNER} of the group.
     * Cannot delete an expense if any of its debts have already been settled ({@code SETTLED}).
     * Deletion order: Debt → ExpensePayer → ExpenseShare → Expense.
     *
     * @param groupId        the group's ID
     * @param expenseId      the expense's ID
     * @param authentication the currently authenticated user
     * @return {@code 200 OK};
     *         {@code 400} if any debt is already SETTLED;
     *         {@code 403} if the caller is neither the creator nor an OWNER
     */
    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete an expense", description = "Deletes an expense along with all associated payers, shares, and pending debts")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Integer groupId,
            @PathVariable Integer expenseId,
            Authentication authentication
    ) {
        DeleteExpenseModel model = expenseMapper.toDeleteExpenseModel(groupId, expenseId, authentication.getName());
        expenseService.delete(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Expense deleted successfully"));
    }
}
