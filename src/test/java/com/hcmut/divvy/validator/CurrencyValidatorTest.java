package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyValidatorTest {

    private final CurrencyValidator validator = new CurrencyValidator();

    @Test
    void validateCurrencyExists_returnsCurrencyWhenPresent() {
        Currency currency = Currency.builder().id(1).name("US Dollar").acronym("USD").build();
        assertThat(validator.validateCurrencyExists(Optional.of(currency), 1)).isEqualTo(currency);
    }

    @Test
    void validateCurrencyExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateCurrencyExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateCurrencyAcronymUnique_throwsWhenAcronymExists() {
        assertThatThrownBy(() -> validator.validateCurrencyAcronymUnique(true))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void validateCurrencyAcronymUnique_passesWhenAcronymDoesNotExist() {
        validator.validateCurrencyAcronymUnique(false);
    }
}
