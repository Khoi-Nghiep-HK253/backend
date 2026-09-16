package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.dto.request.ExpensePayerRequest;
import com.hcmut.divvy.dto.request.ExpenseShareRequest;
import com.hcmut.divvy.entity.enums.SplitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Draft expense data extracted from a receipt photo by AI.
 * Not persisted — the client reviews/edits this and then calls the normal
 * "create expense" endpoint with the (possibly corrected) values.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptScanResponse {

    private String description;
    private BigDecimal totalAmount;
    private Integer currencyId;
    private LocalDate expenseDate;
    private SplitType splitType;
    private List<ExpensePayerRequest> payers;
    private List<ExpenseShareRequest> shares;
    private List<LineItem> lineItems;
    private String notes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineItem {
        private String name;
        private BigDecimal amount;
    }
}
