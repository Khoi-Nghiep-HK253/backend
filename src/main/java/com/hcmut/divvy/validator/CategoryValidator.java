package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryValidator {

    /**
     * Ensures the category exists, returning the Category entity or throwing
     * ResourceNotFoundException.
     *
     * @param categoryOpt pre-fetched Optional<Category> from repository
     * @param categoryId  target category ID
     * @return Category entity
     */
    public Category validateCategoryExists(Optional<Category> categoryOpt, Integer categoryId) {
        return categoryOpt.orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    /**
     * Ensures category name is unique across categories.
     *
     * @param nameExists whether a category with the same name already exists
     */
    public void validateCategoryNameUnique(boolean nameExists) {
        if (nameExists) {
            throw new BusinessException("Category with this name already exists", HttpStatus.CONFLICT);
        }
    }
}
