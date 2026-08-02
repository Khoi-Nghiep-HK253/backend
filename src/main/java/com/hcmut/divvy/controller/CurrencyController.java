package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateCurrencyRequest;
import com.hcmut.divvy.dto.request.UpdateCurrencyRequest;
import com.hcmut.divvy.dto.response.CurrencyResponse;
import com.hcmut.divvy.mapper.CurrencyMapper;
import com.hcmut.divvy.service.CurrencyService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency Management", description = "APIs for listing, viewing, creating, updating, and deleting currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CurrencyMapper currencyMapper;

    /**
     * Retrieve a list of all currencies.
     *
     * @return {@code 200 OK} with a list of CurrencyResponse
     */
    @GetMapping
    @Operation(summary = "Get list of all currencies", description = "Retrieves all supported currencies in the system")
    public ResponseEntity<ApiResponse<List<CurrencyResponse>>> getAllCurrencies() {
        List<CurrencyResponse> currencies = currencyService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(currencies, "Currencies retrieved successfully"));
    }

    /**
     * Retrieve details of a specific currency by its ID.
     *
     * @param id the currency ID
     * @return {@code 200 OK} with CurrencyResponse; {@code 404} if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get currency details", description = "Retrieves detailed information for a single currency record by ID")
    public ResponseEntity<ApiResponse<CurrencyResponse>> getCurrencyById(@PathVariable Integer id) {
        GetCurrencyByIdModel model = currencyMapper.toGetCurrencyByIdModel(id);
        CurrencyResponse currency = currencyService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(currency, "Currency retrieved successfully"));
    }

    /**
     * Create a new currency.
     *
     * @param request the currency creation request payload (name, acronym)
     * @return {@code 201 Created} with CurrencyResponse; {@code 409} if acronym already exists
     */
    @PostMapping
    @Operation(summary = "Create a new currency", description = "Creates a new currency with a unique acronym and name")
    public ResponseEntity<ApiResponse<CurrencyResponse>> createCurrency(@Valid @RequestBody CreateCurrencyRequest request) {
        CreateCurrencyModel model = currencyMapper.toModel(request);
        CurrencyResponse created = currencyService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Currency created successfully"));
    }

    /**
     * Update an existing currency.
     *
     * @param id      the ID of the currency to update
     * @param request the fields to update (name, acronym)
     * @return {@code 200 OK} with the updated CurrencyResponse;
     *         {@code 404} if currency not found; {@code 409} if the acronym is already taken
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a currency", description = "Updates an existing currency's name or acronym")
    public ResponseEntity<ApiResponse<CurrencyResponse>> updateCurrency(
            @PathVariable Integer id,
            @RequestBody UpdateCurrencyRequest request) {
        UpdateCurrencyModel model = currencyMapper.toModel(request, id);
        CurrencyResponse updated = currencyService.update(model);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Currency updated successfully"));
    }

    /**
     * Delete a currency by ID.
     *
     * @param id the currency ID to delete
     * @return {@code 200 OK}; {@code 404} if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a currency", description = "Deletes a currency record by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCurrency(@PathVariable Integer id) {
        DeleteCurrencyModel model = currencyMapper.toDeleteCurrencyModel(id);
        currencyService.delete(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Currency deleted successfully"));
    }
}
