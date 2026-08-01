package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateSettlementRequest;
import com.hcmut.divvy.dto.response.SettlementDetailResponse;
import com.hcmut.divvy.dto.response.SettlementResponse;
import com.hcmut.divvy.dto.response.SettlementSummaryResponse;
import com.hcmut.divvy.mapper.SettlementMapper;
import com.hcmut.divvy.service.SettlementService;
import com.hcmut.divvy.service.model.CreateSettlementModel;
import com.hcmut.divvy.service.model.GetGroupSettlementsModel;
import com.hcmut.divvy.service.model.GetSettlementByIdModel;
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
@RequestMapping("/api/groups/{groupId}/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlement", description = "Group settlement & debt payment recording APIs")
@PreAuthorize("isAuthenticated()")
public class SettlementController {

    private final SettlementService settlementService;
    private final SettlementMapper settlementMapper;

    /**
     * Record a debt payment (create a settlement).
     * <p>
     * The caller must be either the {@code fromUser} or {@code toUser} of the debt.
     * The debt must be in the {@code PENDING} state.
     * {@code amount} must be greater than zero and must not exceed {@code debt.amount}.
     * After the settlement is persisted, the debt status automatically transitions to {@code SETTLED}.
     * <p>
     * Optional fields:
     * <ul>
     *   <li>{@code method} — payment method (defaults to {@code "CASH"}).</li>
     *   <li>{@code paidAt} — timestamp of the payment (defaults to the current time).</li>
     *   <li>{@code note} — free-text note.</li>
     * </ul>
     *
     * @param groupId        the group's ID
     * @param request        settlement payload (debtId, amount, method, note, paidAt)
     * @param authentication the currently authenticated user (must be fromUser or toUser of the debt)
     * @return {@code 201 Created} with SettlementResponse;
     *         {@code 400} if the debt is already SETTLED or the amount is invalid;
     *         {@code 403} if the caller is not a party to the debt;
     *         {@code 404} if the debt does not exist
     */
    @PostMapping
    @Operation(summary = "Record debt payment", description = "Records a settlement transaction for a debt and updates the debt status to SETTLED")
    public ResponseEntity<ApiResponse<SettlementResponse>> createSettlement(
            @PathVariable Integer groupId,
            @Valid @RequestBody CreateSettlementRequest request,
            Authentication authentication
    ) {
        CreateSettlementModel model = settlementMapper.toModel(request, groupId, authentication.getName());
        SettlementResponse response = settlementService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Settlement recorded successfully"));
    }

    /**
     * Retrieve a paginated settlement history for a group, with optional filters.
     * <p>
     * Results are sorted by {@code paidAt} and {@code createdAt} descending.
     * Optional filters: payer ({@code fromUserId}), payee ({@code toUserId}),
     * and date range ({@code fromDate} to {@code toDate} based on {@code paidAt}).
     *
     * @param groupId        the group's ID
     * @param fromUserId     optional filter by payer user ID
     * @param toUserId       optional filter by payee user ID
     * @param fromDate       optional lower bound date (ISO: yyyy-MM-dd)
     * @param toDate         optional upper bound date (ISO: yyyy-MM-dd)
     * @param page           page number (zero-based, default 0)
     * @param size           page size (default 20)
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with {@code Page<SettlementSummaryResponse>};
     *         {@code 403} if the caller is not a member
     */
    @GetMapping
    @Operation(summary = "List group settlements", description = "Gets a paginated history of settlement payments for the group with optional user and date range filters")
    public ResponseEntity<ApiResponse<Page<SettlementSummaryResponse>>> getGroupSettlements(
            @PathVariable Integer groupId,
            @RequestParam(required = false) Integer fromUserId,
            @RequestParam(required = false) Integer toUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paidAt", "createdAt"));
        GetGroupSettlementsModel model = settlementMapper.toGetGroupSettlementsModel(
                groupId, authentication.getName(), fromUserId, toUserId, fromDate, toDate, pageable);

        Page<SettlementSummaryResponse> pageResult = settlementService.getGroupSettlements(model);
        return ResponseEntity.ok(ApiResponse.ok(pageResult, "Settlements retrieved successfully"));
    }

    /**
     * Retrieve the full details of a specific settlement transaction.
     * <p>
     * Verifies that the settlement belongs to the specified group.
     * Returns settlement information together with the related debt details.
     *
     * @param groupId        the group's ID
     * @param settlementId   the settlement's ID
     * @param authentication the currently authenticated user (must be a group member)
     * @return {@code 200 OK} with SettlementDetailResponse;
     *         {@code 404} if the settlement does not exist;
     *         {@code 400} if the settlement does not belong to this group;
     *         {@code 403} if the caller is not a member
     */
    @GetMapping("/{settlementId}")
    @Operation(summary = "Get settlement detail", description = "Retrieves full details of a specific settlement transaction")
    public ResponseEntity<ApiResponse<SettlementDetailResponse>> getSettlementById(
            @PathVariable Integer groupId,
            @PathVariable Integer settlementId,
            Authentication authentication
    ) {
        GetSettlementByIdModel model = settlementMapper.toGetSettlementByIdModel(groupId, settlementId, authentication.getName());
        SettlementDetailResponse response = settlementService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(response, "Settlement retrieved successfully"));
    }
}
