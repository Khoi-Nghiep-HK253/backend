package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Currency;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrencyValidator {

    /**
     * Ensures the currency exists, returning the Currency entity or throwing ResourceNotFoundException.
     *
     * @param currencyOpt pre-fetched Optional<Currency> from repository
     * @param currencyId  target currency ID
     * @return Currency entity
     */
    public Currency validateCurrencyExists(Optional<Currency> currencyOpt, Integer currencyId) {
        return currencyOpt.orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));
    }

    /**
     * Ensures currency acronym is unique across currencies.
     *
     * @param acronymExists whether a currency with the same acronym already exists
     */
    public void validateCurrencyAcronymUnique(boolean acronymExists) {
        if (acronymExists) {
            throw new BusinessException("Currency with this acronym already exists", HttpStatus.CONFLICT);
        }
    }
}
