package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.mapper.DebtMapper;
import com.hcmut.divvy.service.DebtService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/debts")
@RequiredArgsConstructor
@Tag(name = "Debt", description = "Group debt management & tracking APIs")
@PreAuthorize("isAuthenticated()")
public class DebtController {

    private final DebtService debtService;
    private final DebtMapper debtMapper;

    /**
     * Retrieve all debts in a group with optional filters.
     * <p>
     * Debts are created automatically by the system when an expense is created or updated;
     * they are never created manually via this API.
     * Optional filters: status ({@code PENDING} / {@code SETTLED}) and userId
     * (returns debts where the user is either {@code fromUser} or {@code toUser}).
     *
     * @param groupId        the group's ID
     * @param status         optional debt status filter
     * @param userId         optional user ID filter
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with a list of DebtItemResponse;
     *         {@code 403} if the caller is not a member
     */
    @GetMapping
    @Operation(summary = "List group debts", description = "Retrieves a list of debts for the group with optional status and user filters")
    public ResponseEntity<ApiResponse<List<DebtItemResponse>>> getGroupDebts(
            @PathVariable Integer groupId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) Integer userId,
            Authentication authentication
    ) {
        GetGroupDebtsModel model = debtMapper.toGetGroupDebtsModel(groupId, authentication.getName(), status, userId);
        List<DebtItemResponse> response = debtService.getGroupDebts(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Debts retrieved successfully"));
    }

    /**
     * Retrieve an aggregated debt summary grouped by user pairs.
     * <p>
     * Consolidates all {@code PENDING} debts in the group by {@code (fromUser, toUser)} pair
     * and sums their amounts. For example, if A owes B across three different expenses,
     * the result shows a single entry "A → B: total amount".
     *
     * @param groupId        the group's ID
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with DebtGroupSummaryResponse {pairs: [{fromUser, toUser, totalOwed, currency}]};
     *         {@code 403} if the caller is not a member
     */
    @GetMapping("/summary")
    @Operation(summary = "Get group debt summary", description = "Retrieves an aggregated summary table of debts between user pairs in the group")
    public ResponseEntity<ApiResponse<DebtGroupSummaryResponse>> getGroupDebtSummary(
            @PathVariable Integer groupId,
            Authentication authentication
    ) {
        GetGroupDebtSummaryModel model = debtMapper.toGetGroupDebtSummaryModel(groupId, authentication.getName());
        DebtGroupSummaryResponse response = debtService.getGroupDebtSummary(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Debt summary retrieved successfully"));
    }

    /**
     * Retrieve the current user's debts within the group.
     * <p>
     * Returns two categorized lists, both containing only {@code PENDING} debts:
     * <ul>
     *   <li>{@code iOwe} — debts where the caller is {@code fromUser} (I owe others), grouped by creditor.</li>
     *   <li>{@code owedToMe} — debts where the caller is {@code toUser} (others owe me), grouped by debtor.</li>
     * </ul>
     *
     * @param groupId        the group's ID
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with MyDebtsResponse {iOwe: [...], owedToMe: [...]};
     *         {@code 403} if the caller is not a member
     */
    @GetMapping("/me")
    @Operation(summary = "Get my debts in group", description = "Retrieves debts specifically involving the currently authenticated user in the group")
    public ResponseEntity<ApiResponse<MyDebtsResponse>> getMyDebts(
            @PathVariable Integer groupId,
            Authentication authentication
    ) {
        GetMyDebtsModel model = debtMapper.toGetMyDebtsModel(groupId, authentication.getName());
        MyDebtsResponse response = debtService.getMyDebts(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "My debts retrieved successfully"));
    }

    /**
     * Retrieve the details of a specific debt record.
     * <p>
     * Verifies that the debt belongs to the specified group.
     * Returns debt information together with the list of associated settlements.
     *
     * @param groupId        the group's ID
     * @param debtId         the debt's ID
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with DebtDetailResponse (debt + settlements);
     *         {@code 404} if the debt does not exist;
     *         {@code 400} if the debt does not belong to this group;
     *         {@code 403} if the caller is not a member
     */
    @GetMapping("/{debtId}")
    @Operation(summary = "Get debt detail", description = "Retrieves detailed information for a single debt record")
    public ResponseEntity<ApiResponse<DebtDetailResponse>> getDebtById(
            @PathVariable Integer groupId,
            @PathVariable Integer debtId,
            Authentication authentication
    ) {
        GetDebtByIdModel model = debtMapper.toGetDebtByIdModel(groupId, debtId, authentication.getName());
        DebtDetailResponse response = debtService.getDebtById(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Debt retrieved successfully"));
    }
}
