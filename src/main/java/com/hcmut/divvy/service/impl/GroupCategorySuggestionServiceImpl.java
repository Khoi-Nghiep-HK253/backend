package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.dto.response.SuggestCategoryResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.mapper.GroupMapper;
import com.hcmut.divvy.repository.CategoryRepository;
import com.hcmut.divvy.service.GroupCategorySuggestionService;
import com.hcmut.divvy.service.model.GroupCategorySuggestion;
import com.hcmut.divvy.service.model.SuggestGroupCategoryModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupCategorySuggestionServiceImpl implements GroupCategorySuggestionService {

    private static final String SYSTEM_PROMPT = """
            You are an assistant that assigns a category to a new group in a group expense-splitting app. \
            A group is a trip, a household, a recurring cost, or similar — the category describes what kind \
            of group it is, not what expenses will be logged in it. \
            Reply in the same language as the group's name/note (Vietnamese or English).""";

    private final CategoryRepository categoryRepository;
    private final GroupMapper groupMapper;
    private final ChatClient chatClient;

    @Override
    public SuggestCategoryResponse suggest(SuggestGroupCategoryModel model) {
        List<Category> existing = categoryRepository.findAll();
        GroupCategorySuggestion suggestion = suggestCategory(model, existing);

        Category matched = existing.stream()
                .filter(category -> category.getName().equalsIgnoreCase(suggestion.categoryName()))
                .findFirst()
                .orElse(null);

        return groupMapper.toSuggestCategoryResponse(matched, suggestion.categoryName());
    }

    private GroupCategorySuggestion suggestCategory(SuggestGroupCategoryModel model, List<Category> existing) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(buildUserPrompt(model, existing))
                    .call()
                    .entity(GroupCategorySuggestion.class);
        } catch (Exception e) {
            log.error("Group category suggestion call failed", e);
            throw new BusinessException("Could not suggest a category right now. Please try again.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String buildUserPrompt(SuggestGroupCategoryModel model, List<Category> existing) {
        String categoryList = existing.isEmpty()
                ? "(none yet)"
                : existing.stream().map(category -> category.getName()).collect(Collectors.joining(", "));

        return """
                Existing categories: %s

                Group name: %s
                Group note: %s

                Pick the existing category name that best fits, copied exactly as listed. \
                If none of them fit well, propose a new short category name (2-4 words) instead."""
                .formatted(categoryList, model.getName(),
                        model.getNote() == null || model.getNote().isBlank() ? "(none)" : model.getNote());
    }
}
