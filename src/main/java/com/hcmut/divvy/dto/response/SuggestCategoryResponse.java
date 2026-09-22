package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI-suggested category for a new group, shaped so the client can drop it straight
 * into a "create group" request — exactly one of the two fields is set, mirroring
 * {@code CreateGroupRequest}'s {@code categoryId}/{@code categoryName} contract.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestCategoryResponse {

    /** Set when the suggestion matched an existing category. */
    private Integer categoryId;

    /** Set when no existing category fit and a new name is proposed (not persisted). */
    private String categoryName;
}
