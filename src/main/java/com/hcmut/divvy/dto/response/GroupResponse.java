package com.hcmut.divvy.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {
    private Integer id;
    private String name;
    private CategoryInfo category;
    private CurrencyInfo defaultCurrency;
    private String note;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserInfo createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CategoryInfo {
        private Integer id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CurrencyInfo {
        private Integer id;
        private String code;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserInfo {
        private Integer id;
        private String username;
    }
}
