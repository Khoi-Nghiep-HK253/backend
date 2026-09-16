package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.Settlement;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.DebtStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettlementValidatorTest {

    private final SettlementValidator validator = new SettlementValidator();
    private final User fromUser = User.builder().id(1).build();
    private final User toUser = User.builder().id(2).build();

    @Test
    void validateSettlementExists_returnsSettlementWhenPresent() {
        Settlement settlement = Settlement.builder().id(1).build();
        assertThat(validator.validateSettlementExists(Optional.of(settlement), 1)).isEqualTo(settlement);
    }

    @Test
    void validateSettlementExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateSettlementExists(Optional.empty(), 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateSettlementBelongsToGroup_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateSettlementBelongsToGroup(null, 1))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateSettlementBelongsToGroup_throwsWhenGroupIdMismatch() {
        Settlement settlement = Settlement.builder().group(Group.builder().id(1).build()).build();
        assertThatThrownBy(() -> validator.validateSettlementBelongsToGroup(settlement, 2))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateSettlementBelongsToGroup_passesWhenGroupIdMatches() {
        Settlement settlement = Settlement.builder().group(Group.builder().id(1).build()).build();
        validator.validateSettlementBelongsToGroup(settlement, 1);
    }

    @Test
    void validateDebtForSettlement_throwsWhenDebtNull() {
        assertThatThrownBy(() -> validator.validateDebtForSettlement(null, BigDecimal.TEN, 1))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void validateDebtForSettlement_throwsWhenAlreadySettled() {
        Debt debt = Debt.builder().fromUser(fromUser).toUser(toUser).status(DebtStatus.SETTLED)
                .amount(BigDecimal.TEN).build();
        assertThatThrownBy(() -> validator.validateDebtForSettlement(debt, BigDecimal.ONE, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Debt has already been fully settled.");
    }

    @Test
    void validateDebtForSettlement_throwsWhenCallerNotParticipant() {
        Debt debt = Debt.builder().fromUser(fromUser).toUser(toUser).status(DebtStatus.PENDING)
                .amount(BigDecimal.TEN).build();
        assertThatThrownBy(() -> validator.validateDebtForSettlement(debt, BigDecimal.ONE, 99))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateDebtForSettlement_throwsWhenAmountNotPositive() {
        Debt debt = Debt.builder().fromUser(fromUser).toUser(toUser).status(DebtStatus.PENDING)
                .amount(BigDecimal.TEN).build();
        assertThatThrownBy(() -> validator.validateDebtForSettlement(debt, BigDecimal.ZERO, 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Settlement amount must be greater than zero.");
    }

    @Test
    void validateDebtForSettlement_throwsWhenAmountExceedsDebt() {
        Debt debt = Debt.builder().fromUser(fromUser).toUser(toUser).status(DebtStatus.PENDING)
                .amount(BigDecimal.TEN).build();
        assertThatThrownBy(() -> validator.validateDebtForSettlement(debt, new BigDecimal("20"), 1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exceeds remaining debt amount");
    }

    @Test
    void validateDebtForSettlement_passesWhenValid() {
        Debt debt = Debt.builder().fromUser(fromUser).toUser(toUser).status(DebtStatus.PENDING)
                .amount(BigDecimal.TEN).build();
        validator.validateDebtForSettlement(debt, BigDecimal.TEN, 2);
    }
}
