package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.request.ExpensePayerRequest;
import com.hcmut.divvy.dto.request.ExpenseShareRequest;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Expense;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.enums.SplitType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ExpenseValidator {

    public Expense validateExpenseExists(Optional<Expense> expenseOptional, Integer expenseId) {
        return expenseOptional.orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
    }

    public Currency validateCurrencyExists(Optional<Currency> currencyOptional, Integer currencyId) {
        return currencyOptional.orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));
    }

    public Category validateCategoryExists(Optional<Category> categoryOptional, Integer categoryId) {
        if (categoryId == null) return null;
        return categoryOptional.orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    public void validateIsMember(GroupMember member) {
        if (member == null) {
            throw new BusinessException("You are not a member of this group.", HttpStatus.FORBIDDEN);
        }
    }

    public void validatePayers(BigDecimal totalAmount, List<ExpensePayerRequest> payers, Set<Integer> groupUserIds) {
        if (payers == null || payers.isEmpty()) {
            throw new BusinessException("Payers list cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (ExpensePayerRequest payer : payers) {
            if (!groupUserIds.contains(payer.getUserId())) {
                throw new BusinessException(
                        "Payer with userId " + payer.getUserId() + " is not a member of this group.",
                        HttpStatus.BAD_REQUEST);
            }
            if (payer.getAmount() == null || payer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Payer amount must be greater than zero.", HttpStatus.BAD_REQUEST);
            }
            sum = sum.add(payer.getAmount());
        }

        if (sum.compareTo(totalAmount) != 0) {
            throw new BusinessException(
                    "Sum of payer amounts (" + sum + ") must equal totalAmount (" + totalAmount + ").",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void validateShares(SplitType splitType, BigDecimal totalAmount, List<ExpenseShareRequest> shares,
            Set<Integer> groupUserIds) {
        if (shares == null || shares.isEmpty()) {
            throw new BusinessException("Shares list cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        for (ExpenseShareRequest share : shares) {
            if (!groupUserIds.contains(share.getUserId())) {
                throw new BusinessException(
                        "Participant with userId " + share.getUserId() + " is not a member of this group.",
                        HttpStatus.BAD_REQUEST);
            }
        }

        SplitType type = splitType != null ? splitType : SplitType.EQUAL;

        switch (type) {
            case EXACT:
                BigDecimal exactSum = BigDecimal.ZERO;
                for (ExpenseShareRequest share : shares) {
                    if (share.getAmount() == null || share.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessException("Exact share amount must be provided and non-negative.",
                                HttpStatus.BAD_REQUEST);
                    }
                    exactSum = exactSum.add(share.getAmount());
                }
                if (exactSum.compareTo(totalAmount) != 0) {
                    throw new BusinessException(
                            "Sum of share amounts (" + exactSum + ") must equal totalAmount (" + totalAmount + ").",
                            HttpStatus.BAD_REQUEST);
                }
                break;

            case PERCENTAGE:
                BigDecimal percentageSum = BigDecimal.ZERO;
                for (ExpenseShareRequest share : shares) {
                    if (share.getPercentage() == null || share.getPercentage().compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessException(
                                "Percentage must be provided and non-negative for PERCENTAGE split mode.",
                                HttpStatus.BAD_REQUEST);
                    }
                    percentageSum = percentageSum.add(share.getPercentage());
                }
                if (percentageSum.compareTo(new BigDecimal("100.0")) != 0
                        && percentageSum.compareTo(new BigDecimal("100")) != 0) {
                    throw new BusinessException("Sum of share percentages (" + percentageSum + "%) must equal 100%.",
                            HttpStatus.BAD_REQUEST);
                }
                break;

            case SHARES:
                BigDecimal totalRatio = BigDecimal.ZERO;
                for (ExpenseShareRequest share : shares) {
                    if (share.getRatio() == null || share.getRatio().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("Ratio must be provided and greater than 0 for SHARES split mode.",
                                HttpStatus.BAD_REQUEST);
                    }
                    totalRatio = totalRatio.add(share.getRatio());
                }
                if (totalRatio.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException("Total ratio shares must be greater than 0.", HttpStatus.BAD_REQUEST);
                }
                break;

            case ADJUSTMENT:
                BigDecimal totalAdjustments = BigDecimal.ZERO;
                for (ExpenseShareRequest share : shares) {
                    if (share.getAdjustment() != null) {
                        totalAdjustments = totalAdjustments.add(share.getAdjustment());
                    }
                }
                if (totalAdjustments.compareTo(BigDecimal.ZERO) != 0) {
                    throw new BusinessException("Sum of share adjustments (" + totalAdjustments + ") must equal 0.",
                            HttpStatus.BAD_REQUEST);
                }
                break;

            case EQUAL:
            default:
                break;
        }
    }

    public void validateModificationAuth(Expense expense, Integer callerUserId, boolean isCallerAdmin) {
        if (!isCallerAdmin) {
            throw new BusinessException("You are not authorized to modify or delete this expense.",
                    HttpStatus.FORBIDDEN);
        }
    }

    public void validateDeletableOrEditable(boolean hasSettledDebts) {
        if (hasSettledDebts) {
            throw new BusinessException("Expense cannot be modified or deleted because it contains settled debts.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
