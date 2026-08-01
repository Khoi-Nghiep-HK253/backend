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
