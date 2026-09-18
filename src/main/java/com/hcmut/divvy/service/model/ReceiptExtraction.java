package com.hcmut.divvy.service.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Structured-output schema the AI model fills in from a receipt photo.
 * Public only so {@code ExpenseMapper} can map it — never exposed via the API
 * directly, always mapped into {@link com.hcmut.divvy.dto.response.ReceiptScanResponse}.
 */
public record ReceiptExtraction(
        String merchantName,
        BigDecimal totalAmount,
        String currencyCode,
        List<Item> items) {

    public record Item(String name, BigDecimal amount) {
    }
}
