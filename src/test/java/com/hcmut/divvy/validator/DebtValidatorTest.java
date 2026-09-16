package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.Expense;
import com.hcmut.divvy.entity.Group;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DebtValidatorTest {

    private final DebtValidator validator = new DebtValidator();

    @Test
    void validateDebtExists_returnsDebtWhenPresent() {
        Debt debt = Debt.builder().id(1).build();
        assertThat(validator.validateDebtExists(Optional.of(debt), 1)).isEqualTo(debt);
    }

    @Test
    void validateDebtExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateDebtExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateDebtBelongsToGroup_throwsWhenDebtNull() {
        assertThatThrownBy(() -> validator.validateDebtBelongsToGroup(null, 1))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateDebtBelongsToGroup_throwsWhenGroupIdMismatch() {
        Group group = Group.builder().id(1).build();
        Expense expense = Expense.builder().group(group).build();
        Debt debt = Debt.builder().expense(expense).build();

        assertThatThrownBy(() -> validator.validateDebtBelongsToGroup(debt, 2))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Debt does not belong to the specified group.");
    }

    @Test
    void validateDebtBelongsToGroup_passesWhenGroupIdMatches() {
        Group group = Group.builder().id(1).build();
        Expense expense = Expense.builder().group(group).build();
        Debt debt = Debt.builder().expense(expense).build();

        validator.validateDebtBelongsToGroup(debt, 1);
    }
}
