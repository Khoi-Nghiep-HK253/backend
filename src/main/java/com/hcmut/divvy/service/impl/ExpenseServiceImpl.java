package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.request.ExpensePayerRequest;
import com.hcmut.divvy.dto.request.ExpenseShareRequest;
import com.hcmut.divvy.dto.response.ExpenseResponse;
import com.hcmut.divvy.dto.response.ExpenseSummaryResponse;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.Expense;
import com.hcmut.divvy.entity.ExpensePayer;
import com.hcmut.divvy.entity.ExpenseShare;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.entity.enums.GroupRole;
import com.hcmut.divvy.entity.enums.SplitType;
import com.hcmut.divvy.mapper.ExpenseMapper;
import com.hcmut.divvy.repository.*;
import com.hcmut.divvy.service.ExpenseService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.ExpenseValidator;
import com.hcmut.divvy.validator.GroupValidator;
import com.hcmut.divvy.validator.UserValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final CurrencyRepository currencyRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpensePayerRepository expensePayerRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final DebtRepository debtRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;

    @Override
    @Transactional
    public ExpenseResponse create(CreateExpenseModel model) {
        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Currency currency = expenseValidator.validateCurrencyExists(currencyRepository.findById(model.getCurrencyId()),
                model.getCurrencyId());

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(group.getId());
        Map<Integer, User> userMap = groupMembers.stream()
                .collect(Collectors.toMap(m -> m.getUser().getId(), GroupMember::getUser));

        expenseValidator.validatePayers(model.getTotalAmount(), model.getPayers(), userMap.keySet());
        expenseValidator.validateShares(model.getSplitType(), model.getTotalAmount(), model.getShares(),
                userMap.keySet());

        SplitType splitType = model.getSplitType() != null ? model.getSplitType() : SplitType.EQUAL;

        Expense expense = expenseMapper.toEntity(model, group, currency, splitType, caller);

        Expense savedExpense = expenseRepository.save(expense);

        // Map & save payers
        List<ExpensePayer> savedPayers = savePayers(savedExpense, model.getPayers(), userMap);

        // Calculate normalized share amounts
        Map<Integer, BigDecimal> normalizedSharesMap = calculateNormalizedShares(splitType, model.getTotalAmount(),
                model.getShares());
        List<ExpenseShare> savedShares = saveShares(savedExpense, normalizedSharesMap, userMap);

        // Compute and save Debts
        List<Debt> savedDebts = calculateAndSaveDebts(savedExpense, model.getPayers(), normalizedSharesMap, userMap);

        return expenseMapper.toExpenseResponse(savedExpense, savedPayers, savedShares, savedDebts);
    }

    @Override
    public Page<ExpenseSummaryResponse> getGroupExpenses(GetGroupExpensesModel model) {
        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Specification<Expense> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("group").get("id"), group.getId()));

            if (model.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), model.getFromDate()));
            }
            if (model.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), model.getToDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Expense> expensePage = expenseRepository.findAll(spec, model.getPageable());

        return expensePage.map(expense -> {
            List<ExpensePayer> payers = expensePayerRepository.findByExpenseId(expense.getId());
            int payerCount = payers.size();
            int shareCount = expenseShareRepository.findByExpenseId(expense.getId()).size();
            String createdByName = expense.getCreatedBy() != null
                    ? expense.getCreatedBy().getUsername()
                    : (!payers.isEmpty() ? payers.get(0).getUser().getUsername() : null);
            return expenseMapper.toSummaryResponse(expense, payerCount, shareCount, createdByName);
        });
    }

    @Override
    public ExpenseResponse findById(GetExpenseByIdModel model) {
        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Expense expense = expenseValidator.validateExpenseExists(expenseRepository.findById(model.getExpenseId()),
                model.getExpenseId());

        List<ExpensePayer> payers = expensePayerRepository.findByExpenseId(expense.getId());
        List<ExpenseShare> shares = expenseShareRepository.findByExpenseId(expense.getId());
        List<Debt> debts = debtRepository.findByExpenseId(expense.getId());

        return expenseMapper.toExpenseResponse(expense, payers, shares, debts);
    }

    @Override
    @Transactional
    public ExpenseResponse update(UpdateExpenseModel model) {
        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Expense expense = expenseValidator.validateExpenseExists(expenseRepository.findById(model.getExpenseId()),
                model.getExpenseId());

        boolean isCallerAdmin = callerMember != null && GroupRole.OWNER == callerMember.getRole();
        expenseValidator.validateModificationAuth(expense, caller.getId(), isCallerAdmin);

        boolean hasSettled = debtRepository.existsByExpenseIdAndStatusNot(expense.getId(), DebtStatus.PENDING);
        expenseValidator.validateDeletableOrEditable(hasSettled);

        Currency currency = expenseValidator.validateCurrencyExists(currencyRepository.findById(model.getCurrencyId()),
                model.getCurrencyId());

        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(group.getId());
        Map<Integer, User> userMap = groupMembers.stream()
                .collect(Collectors.toMap(m -> m.getUser().getId(), GroupMember::getUser));

        expenseValidator.validatePayers(model.getTotalAmount(), model.getPayers(), userMap.keySet());
        expenseValidator.validateShares(model.getSplitType(), model.getTotalAmount(), model.getShares(),
                userMap.keySet());

        SplitType splitType = model.getSplitType() != null ? model.getSplitType() : SplitType.EQUAL;

        expenseMapper.updateEntity(model, currency, splitType, expense);

        Expense updatedExpense = expenseRepository.save(expense);

        // Delete existing Payers, Shares, and Debts
        debtRepository.deleteByExpenseId(updatedExpense.getId());
        expensePayerRepository.deleteByExpenseId(updatedExpense.getId());
        expenseShareRepository.deleteByExpenseId(updatedExpense.getId());

        // Re-save Payers, Shares, and Debts
        List<ExpensePayer> savedPayers = savePayers(updatedExpense, model.getPayers(), userMap);
        Map<Integer, BigDecimal> normalizedSharesMap = calculateNormalizedShares(splitType, model.getTotalAmount(),
                model.getShares());
        List<ExpenseShare> savedShares = saveShares(updatedExpense, normalizedSharesMap, userMap);
        List<Debt> savedDebts = calculateAndSaveDebts(updatedExpense, model.getPayers(), normalizedSharesMap, userMap);

        return expenseMapper.toExpenseResponse(updatedExpense, savedPayers, savedShares, savedDebts);
    }

    @Override
    @Transactional
    public void delete(DeleteExpenseModel model) {
        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Expense expense = expenseValidator.validateExpenseExists(expenseRepository.findById(model.getExpenseId()),
                model.getExpenseId());

        boolean isCallerAdmin = callerMember != null && GroupRole.OWNER == callerMember.getRole();
        expenseValidator.validateModificationAuth(expense, caller.getId(), isCallerAdmin);

        boolean hasSettled = debtRepository.existsByExpenseIdAndStatusNot(expense.getId(), DebtStatus.PENDING);
        expenseValidator.validateDeletableOrEditable(hasSettled);

        debtRepository.deleteByExpenseId(expense.getId());
        expensePayerRepository.deleteByExpenseId(expense.getId());
        expenseShareRepository.deleteByExpenseId(expense.getId());
        expenseRepository.delete(expense);
    }

    // ── Helper Methods
    // ─────────────────────────────────────────────────────────────

    private List<ExpensePayer> savePayers(Expense expense, List<ExpensePayerRequest> payerRequests,
            Map<Integer, User> userMap) {
        List<ExpensePayer> payers = new ArrayList<>();
        for (ExpensePayerRequest req : payerRequests) {
            User user = userValidator.validateUserInMap(userMap, req.getUserId());
            payers.add(expenseMapper.toPayerEntity(expense, user, req.getAmount()));
        }
        return expensePayerRepository.saveAll(payers);
    }

    private Map<Integer, BigDecimal> calculateNormalizedShares(SplitType splitType, BigDecimal totalAmount,
            List<ExpenseShareRequest> shares) {
        Map<Integer, BigDecimal> result = new LinkedHashMap<>();
        int count = shares.size();
        if (count == 0)
            return result;

        SplitType type = splitType != null ? splitType : SplitType.EQUAL;

        switch (type) {
            case EQUAL: {
                BigDecimal equalShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                BigDecimal sum = equalShare.multiply(BigDecimal.valueOf(count));
                BigDecimal remainder = totalAmount.subtract(sum);

                for (int i = 0; i < count; i++) {
                    ExpenseShareRequest req = shares.get(i);
                    BigDecimal amount = (i == 0) ? equalShare.add(remainder) : equalShare;
                    result.put(req.getUserId(), amount);
                }
                break;
            }
            case EXACT: {
                for (ExpenseShareRequest req : shares) {
                    result.put(req.getUserId(), req.getAmount());
                }
                break;
            }
            case PERCENTAGE: {
                BigDecimal calculatedSum = BigDecimal.ZERO;
                List<Integer> userIds = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    ExpenseShareRequest req = shares.get(i);
                    userIds.add(req.getUserId());
                    BigDecimal shareAmount = totalAmount.multiply(req.getPercentage())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    result.put(req.getUserId(), shareAmount);
                    calculatedSum = calculatedSum.add(shareAmount);
                }
                BigDecimal remainder = totalAmount.subtract(calculatedSum);
                if (remainder.compareTo(BigDecimal.ZERO) != 0 && !userIds.isEmpty()) {
                    Integer firstUser = userIds.get(0);
                    result.put(firstUser, result.get(firstUser).add(remainder));
                }
                break;
            }
            case SHARES: {
                BigDecimal totalRatios = BigDecimal.ZERO;
                for (ExpenseShareRequest req : shares) {
                    totalRatios = totalRatios.add(req.getRatio());
                }
                BigDecimal calculatedSum = BigDecimal.ZERO;
                List<Integer> userIds = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    ExpenseShareRequest req = shares.get(i);
                    userIds.add(req.getUserId());
                    BigDecimal shareAmount = totalAmount.multiply(req.getRatio())
                            .divide(totalRatios, 2, RoundingMode.HALF_UP);
                    result.put(req.getUserId(), shareAmount);
                    calculatedSum = calculatedSum.add(shareAmount);
                }
                BigDecimal remainder = totalAmount.subtract(calculatedSum);
                if (remainder.compareTo(BigDecimal.ZERO) != 0 && !userIds.isEmpty()) {
                    Integer firstUser = userIds.get(0);
                    result.put(firstUser, result.get(firstUser).add(remainder));
                }
                break;
            }
            case ADJUSTMENT: {
                BigDecimal equalBase = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                BigDecimal baseSum = equalBase.multiply(BigDecimal.valueOf(count));
                BigDecimal baseRemainder = totalAmount.subtract(baseSum);

                for (int i = 0; i < count; i++) {
                    ExpenseShareRequest req = shares.get(i);
                    BigDecimal adj = req.getAdjustment() != null ? req.getAdjustment() : BigDecimal.ZERO;
                    BigDecimal userShare = equalBase.add(adj);
                    if (i == 0) {
                        userShare = userShare.add(baseRemainder);
                    }
                    result.put(req.getUserId(), userShare);
                }
                break;
            }
        }
        return result;
    }

    private List<ExpenseShare> saveShares(Expense expense, Map<Integer, BigDecimal> normalizedSharesMap,
            Map<Integer, User> userMap) {
        List<ExpenseShare> shares = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : normalizedSharesMap.entrySet()) {
            User user = userValidator.validateUserInMap(userMap, entry.getKey());
            shares.add(expenseMapper.toShareEntity(expense, user, entry.getValue()));
        }
        return expenseShareRepository.saveAll(shares);
    }

    private List<Debt> calculateAndSaveDebts(Expense expense, List<ExpensePayerRequest> payers,
            Map<Integer, BigDecimal> sharesMap, Map<Integer, User> userMap) {
        Map<Integer, BigDecimal> paidMap = new HashMap<>();
        for (ExpensePayerRequest p : payers) {
            paidMap.put(p.getUserId(), paidMap.getOrDefault(p.getUserId(), BigDecimal.ZERO).add(p.getAmount()));
        }

        Set<Integer> allParticipants = new HashSet<>();
        allParticipants.addAll(paidMap.keySet());
        allParticipants.addAll(sharesMap.keySet());

        List<UserBalanceModel> debtors = new ArrayList<>();
        List<UserBalanceModel> creditors = new ArrayList<>();

        for (Integer userId : allParticipants) {
            BigDecimal paid = paidMap.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal share = sharesMap.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal net = paid.subtract(share); // net = paid - share

            if (net.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new UserBalanceModel(userId, net.abs()));
            } else if (net.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new UserBalanceModel(userId, net));
            }
        }

        List<Debt> debtsToSave = new ArrayList<>();
        int dIdx = 0;
        int cIdx = 0;

        while (dIdx < debtors.size() && cIdx < creditors.size()) {
            UserBalanceModel debtor = debtors.get(dIdx);
            UserBalanceModel creditor = creditors.get(cIdx);

            BigDecimal amountToSettle = debtor.getAmount().min(creditor.getAmount());

            if (amountToSettle.compareTo(BigDecimal.ZERO) > 0) {
                User fromUser = userValidator.validateUserInMap(userMap, debtor.getUserId());
                User toUser = userValidator.validateUserInMap(userMap, creditor.getUserId());

                debtsToSave.add(expenseMapper.toDebt(expense, fromUser, toUser, amountToSettle, DebtStatus.PENDING));
            }

            debtor.subtractAmount(amountToSettle);
            creditor.subtractAmount(amountToSettle);

            if (debtor.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                dIdx++;
            }
            if (creditor.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                cIdx++;
            }
        }

        return debtRepository.saveAll(debtsToSave);
    }
}
