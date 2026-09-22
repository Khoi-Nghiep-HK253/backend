package com.hcmut.divvy.service.model;

/**
 * Structured-output schema the AI model fills in to suggest a group category.
 * Public only so {@code GroupMapper} can map it — never exposed via the API
 * directly, always resolved against existing categories first
 * (see {@code GroupCategorySuggestionServiceImpl}).
 */
public record GroupCategorySuggestion(String categoryName) {
}
