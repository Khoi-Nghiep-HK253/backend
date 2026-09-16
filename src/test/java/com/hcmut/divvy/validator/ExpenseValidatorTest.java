package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.request.ExpensePayerRequest;
import com.hcmut.divvy.dto.request.ExpenseShareRequest;
import com.hcmut.divvy.entity.Expense;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.SplitType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseValidatorTest {

    private final ExpenseValidator validator = new ExpenseValidator();
    private final Set<Integer> groupUserIds = Set.of(1, 2);

    @Test
    void validateExpenseExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateExpenseExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateIsMember_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateIsMember(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateIsMember_passesWhenPresent() {
        validator.validateIsMember(GroupMember.builder().build());
    }

    @Test
    void validatePayers_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validatePayers(BigDecimal.TEN, List.of(), groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payers list cannot be empty.");
    }

    @Test
    void validatePayers_throwsWhenPayerNotGroupMember() {
        List<ExpensePayerRequest> payers = List.of(
                ExpensePayerRequest.builder().userId(99).amount(BigDecimal.TEN).build());
        assertThatThrownBy(() -> validator.validatePayers(BigDecimal.TEN, payers, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is not a member of this group");
    }

    @Test
    void validatePayers_throwsWhenAmountNotPositive() {
        List<ExpensePayerRequest> payers = List.of(
                ExpensePayerRequest.builder().userId(1).amount(BigDecimal.ZERO).build());
        assertThatThrownBy(() -> validator.validatePayers(BigDecimal.ZERO, payers, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Payer amount must be greater than zero.");
    }

    @Test
    void validatePayers_throwsWhenSumDoesNotMatchTotal() {
        List<ExpensePayerRequest> payers = List.of(
                ExpensePayerRequest.builder().userId(1).amount(BigDecimal.TEN).build());
        assertThatThrownBy(() -> validator.validatePayers(new BigDecimal("20"), payers, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must equal totalAmount");
    }

    @Test
    void validatePayers_passesWhenSumMatchesTotal() {
        List<ExpensePayerRequest> payers = List.of(
                ExpensePayerRequest.builder().userId(1).amount(new BigDecimal("6")).build(),
                ExpensePayerRequest.builder().userId(2).amount(new BigDecimal("4")).build());
        validator.validatePayers(BigDecimal.TEN, payers, groupUserIds);
    }

    @Test
    void validateShares_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateShares(SplitType.EQUAL, BigDecimal.TEN, List.of(), groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Shares list cannot be empty.");
    }

    @Test
    void validateShares_throwsWhenParticipantNotGroupMember() {
        List<ExpenseShareRequest> shares = List.of(ExpenseShareRequest.builder().userId(99).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.EQUAL, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is not a member of this group");
    }

    @Test
    void validateShares_equalSplit_passesWithoutAmounts() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).build(),
                ExpenseShareRequest.builder().userId(2).build());
        validator.validateShares(SplitType.EQUAL, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateShares_nullSplitType_defaultsToEqual() {
        List<ExpenseShareRequest> shares = List.of(ExpenseShareRequest.builder().userId(1).build());
        validator.validateShares(null, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateShares_exact_throwsWhenSumMismatch() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).amount(new BigDecimal("3")).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.EXACT, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must equal totalAmount");
    }

    @Test
    void validateShares_exact_throwsWhenAmountMissing() {
        List<ExpenseShareRequest> shares = List.of(ExpenseShareRequest.builder().userId(1).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.EXACT, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Exact share amount must be provided and non-negative.");
    }

    @Test
    void validateShares_exact_passesWhenSumMatches() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).amount(new BigDecimal("6")).build(),
                ExpenseShareRequest.builder().userId(2).amount(new BigDecimal("4")).build());
        validator.validateShares(SplitType.EXACT, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateShares_percentage_throwsWhenSumNot100() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).percentage(new BigDecimal("40")).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.PERCENTAGE, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must equal 100%");
    }

    @Test
    void validateShares_percentage_passesWhenSumIs100() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).percentage(new BigDecimal("60")).build(),
                ExpenseShareRequest.builder().userId(2).percentage(new BigDecimal("40")).build());
        validator.validateShares(SplitType.PERCENTAGE, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateShares_shares_throwsWhenRatioNotPositive() {
        List<ExpenseShareRequest> shares = List
                .of(ExpenseShareRequest.builder().userId(1).ratio(BigDecimal.ZERO).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.SHARES, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ratio must be provided");
    }

    @Test
    void validateShares_shares_passesWithPositiveRatios() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).ratio(BigDecimal.ONE).build(),
                ExpenseShareRequest.builder().userId(2).ratio(new BigDecimal("2")).build());
        validator.validateShares(SplitType.SHARES, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateShares_adjustment_throwsWhenSumNotZero() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).adjustment(new BigDecimal("5")).build());
        assertThatThrownBy(() -> validator.validateShares(SplitType.ADJUSTMENT, BigDecimal.TEN, shares, groupUserIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must equal 0");
    }

    @Test
    void validateShares_adjustment_passesWhenSumIsZero() {
        List<ExpenseShareRequest> shares = List.of(
                ExpenseShareRequest.builder().userId(1).adjustment(new BigDecimal("5")).build(),
                ExpenseShareRequest.builder().userId(2).adjustment(new BigDecimal("-5")).build());
        validator.validateShares(SplitType.ADJUSTMENT, BigDecimal.TEN, shares, groupUserIds);
    }

    @Test
    void validateModificationAuth_throwsWhenNotCallerAdmin() {
        Expense expense = Expense.builder().build();
        assertThatThrownBy(() -> validator.validateModificationAuth(expense, 1, false))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateModificationAuth_passesWhenCallerAdmin() {
        Expense expense = Expense.builder().build();
        validator.validateModificationAuth(expense, 1, true);
    }

    @Test
    void validateDeletableOrEditable_throwsWhenHasSettledDebts() {
        assertThatThrownBy(() -> validator.validateDeletableOrEditable(true))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateDeletableOrEditable_passesWhenNoSettledDebts() {
        validator.validateDeletableOrEditable(false);
    }
}
