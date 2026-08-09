package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.Settlement;
import com.hcmut.divvy.entity.enums.DebtStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class SettlementValidator {

    public Settlement validateSettlementExists(Optional<Settlement> settlementOptional, Integer settlementId) {
        return settlementOptional.orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", settlementId));
    }

    public void validateSettlementBelongsToGroup(Settlement settlement, Integer groupId) {
        if (settlement == null || settlement.getGroup() == null || !settlement.getGroup().getId().equals(groupId)) {
            throw new BusinessException("Settlement does not belong to the specified group.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateDebtForSettlement(Debt debt, BigDecimal amount, Integer callerUserId) {
        if (debt == null) {
            throw new BusinessException("Debt not found.", HttpStatus.NOT_FOUND);
        }

        if (debt.getStatus() == DebtStatus.SETTLED) {
            throw new BusinessException("Debt has already been fully settled.", HttpStatus.BAD_REQUEST);
        }

        if (!debt.getFromUser().getId().equals(callerUserId) && !debt.getToUser().getId().equals(callerUserId)) {
            throw new BusinessException("You are not authorized to record settlement for this debt.",
                    HttpStatus.FORBIDDEN);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Settlement amount must be greater than zero.", HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(debt.getAmount()) > 0) {
            throw new BusinessException(
                    "Settlement amount (" + amount + ") exceeds remaining debt amount (" + debt.getAmount() + ").",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
