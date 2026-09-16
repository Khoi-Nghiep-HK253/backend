package com.hcmut.divvy.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Structured-output schema the AI model fills in from a receipt photo.
 * Public only so {@code ExpenseMapper} can map it — never exposed via the API
 * directly, always mapped into {@link com.hcmut.divvy.dto.response.ReceiptScanResponse}.
 */
public record ReceiptExtraction(
        String merchantName,
        LocalDate expenseDate,
        BigDecimal totalAmount,
        List<LineItem> lineItems,
        String notes) {

    public record LineItem(String name, BigDecimal amount) {
    }
}
