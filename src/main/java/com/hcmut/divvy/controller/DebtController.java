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
