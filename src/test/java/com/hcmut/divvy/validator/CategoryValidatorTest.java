package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryValidatorTest {

    private final CategoryValidator validator = new CategoryValidator();

    @Test
    void validateCategoryExists_returnsCategoryWhenPresent() {
        Category category = Category.builder().id(1).name("Food").build();
        assertThat(validator.validateCategoryExists(Optional.of(category), 1)).isEqualTo(category);
    }

    @Test
    void validateCategoryExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateCategoryExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateCategoryNameUnique_throwsWhenNameExists() {
        assertThatThrownBy(() -> validator.validateCategoryNameUnique(true))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void validateCategoryNameUnique_passesWhenNameDoesNotExist() {
        validator.validateCategoryNameUnique(false);
    }
}
