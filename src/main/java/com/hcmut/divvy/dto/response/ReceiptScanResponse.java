package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data read from a receipt photo by AI, used to pre-fill the "create expense" form.
 * Not persisted — the client lets the user pick payers, shares and split type,
 * then calls the normal "create expense" endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptScanResponse {

    private String description;
    private BigDecimal totalAmount;
    private CurrencyInfo currency;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private String name;
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrencyInfo {
        private Integer id;
        private String name;
        private String acronym;
    }
}
