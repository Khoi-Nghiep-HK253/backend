package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.Debt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DebtValidator {

    public Debt validateDebtExists(Optional<Debt> debtOptional, Integer debtId) {
        return debtOptional.orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
    }

    public void validateDebtBelongsToGroup(Debt debt, Integer groupId) {
        if (debt == null || debt.getExpense() == null || debt.getExpense().getGroup() == null
                || !debt.getExpense().getGroup().getId().equals(groupId)) {
            throw new BusinessException("Debt does not belong to the specified group.", HttpStatus.BAD_REQUEST);
        }
    }
}
