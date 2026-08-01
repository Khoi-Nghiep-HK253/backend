package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.mapper.DebtMapper;
import com.hcmut.divvy.repository.DebtRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.DebtService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.DebtValidator;
import com.hcmut.divvy.validator.ExpenseValidator;
import com.hcmut.divvy.validator.GroupValidator;
import com.hcmut.divvy.validator.UserValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DebtServiceImpl implements DebtService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final ExpenseValidator expenseValidator;
    private final DebtValidator debtValidator;

    @Override
    public List<DebtItemResponse> getGroupDebts(GetGroupDebtsModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Specification<Debt> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("expense").get("group").get("id"), group.getId()));

            if (model.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), model.getStatus()));
            }
            if (model.getUserId() != null) {
                Predicate isFrom = cb.equal(root.get("fromUser").get("id"), model.getUserId());
                Predicate isTo = cb.equal(root.get("toUser").get("id"), model.getUserId());
                predicates.add(cb.or(isFrom, isTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Debt> debts = debtRepository.findAll(spec);
        return debts.stream().map(debtMapper::toDebtItemResponse).toList();
    }

    @Override
    public DebtGroupSummaryResponse getGroupDebtSummary(GetGroupDebtSummaryModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        List<Debt> pendingDebts = debtRepository.findByExpenseGroupIdAndStatus(group.getId(), DebtStatus.PENDING);

        Map<String, DebtPairAggregate> summaryMap = new LinkedHashMap<>();

        String defaultCurrencyCode = group.getDefaultCurrency() != null ? group.getDefaultCurrency().getAcronym() : "VND";

        for (Debt debt : pendingDebts) {
            String key = debt.getFromUser().getId() + "_" + debt.getToUser().getId();
            summaryMap.computeIfAbsent(key, k -> new DebtPairAggregate(debt.getFromUser(), debt.getToUser(), defaultCurrencyCode))
                    .addAmount(debt.getAmount());
        }

        List<DebtPairSummaryResponse> pairs = summaryMap.values().stream()
                .map(agg -> DebtPairSummaryResponse.builder()
                        .fromUser(debtMapper.toUserInfoResponse(agg.fromUser))
                        .toUser(debtMapper.toUserInfoResponse(agg.toUser))
                        .totalOwed(agg.totalOwed)
                        .currency(DebtPairSummaryResponse.CurrencyInfo.builder().code(agg.currencyCode).build())
                        .build())
                .toList();

        return DebtGroupSummaryResponse.builder().pairs(pairs).build();
    }

    @Override
    public MyDebtsResponse getMyDebts(GetMyDebtsModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Specification<Debt> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("expense").get("group").get("id"), group.getId()));
            predicates.add(cb.equal(root.get("status"), DebtStatus.PENDING));

            Predicate isFrom = cb.equal(root.get("fromUser").get("id"), caller.getId());
            Predicate isTo = cb.equal(root.get("toUser").get("id"), caller.getId());
            predicates.add(cb.or(isFrom, isTo));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Debt> myDebtsList = debtRepository.findAll(spec);

        Map<Integer, MyIOweGroupBuilder> iOweMap = new LinkedHashMap<>();
        Map<Integer, MyOwedToMeGroupBuilder> owedToMeMap = new LinkedHashMap<>();

        for (Debt debt : myDebtsList) {
            if (debt.getFromUser().getId().equals(caller.getId())) {
                // I owe debt.getToUser()
                Integer toUserId = debt.getToUser().getId();
                iOweMap.computeIfAbsent(toUserId, k -> new MyIOweGroupBuilder(debt.getToUser()))
                        .addDebt(debt);
            } else if (debt.getToUser().getId().equals(caller.getId())) {
                // debt.getFromUser() owes me
                Integer fromUserId = debt.getFromUser().getId();
                owedToMeMap.computeIfAbsent(fromUserId, k -> new MyOwedToMeGroupBuilder(debt.getFromUser()))
                        .addDebt(debt);
            }
        }

        List<MyDebtsResponse.IOweGroupResponse> iOweList = iOweMap.values().stream()
                .map(builder -> MyDebtsResponse.IOweGroupResponse.builder()
                        .toUser(debtMapper.toUserInfoResponse(builder.toUser))
                        .totalAmount(builder.totalAmount)
                        .debts(builder.subItems)
                        .build())
                .toList();

        List<MyDebtsResponse.OwedToMeGroupResponse> owedToMeList = owedToMeMap.values().stream()
                .map(builder -> MyDebtsResponse.OwedToMeGroupResponse.builder()
                        .fromUser(debtMapper.toUserInfoResponse(builder.fromUser))
                        .totalAmount(builder.totalAmount)
                        .debts(builder.subItems)
                        .build())
                .toList();

        return MyDebtsResponse.builder()
                .iOwe(iOweList)
                .owedToMe(owedToMeList)
                .build();
    }

    @Override
    public DebtDetailResponse getDebtById(GetDebtByIdModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Debt debt = debtValidator.validateDebtExists(
                debtRepository.findById(model.getDebtId()), model.getDebtId());
        debtValidator.validateDebtBelongsToGroup(debt, group.getId());

        return debtMapper.toDebtDetailResponse(debt, List.of());
    }

    // ── Helper Data Classes ────────────────────────────────────────────────────────

    private static class DebtPairAggregate {
        User fromUser;
        User toUser;
        String currencyCode;
        BigDecimal totalOwed = BigDecimal.ZERO;

        DebtPairAggregate(User fromUser, User toUser, String currencyCode) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.currencyCode = currencyCode;
        }

        void addAmount(BigDecimal amount) {
            if (amount != null) {
                this.totalOwed = this.totalOwed.add(amount);
            }
        }
    }

    private static class MyIOweGroupBuilder {
        User toUser;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<MyDebtsResponse.DebtSubItemResponse> subItems = new ArrayList<>();

        MyIOweGroupBuilder(User toUser) {
            this.toUser = toUser;
        }

        void addDebt(Debt debt) {
            if (debt != null && debt.getAmount() != null) {
                this.totalAmount = this.totalAmount.add(debt.getAmount());
                subItems.add(MyDebtsResponse.DebtSubItemResponse.builder()
                        .id(debt.getId())
                        .amount(debt.getAmount())
                        .expenseId(debt.getExpense() != null ? debt.getExpense().getId() : null)
                        .build());
            }
        }
    }

    private static class MyOwedToMeGroupBuilder {
        User fromUser;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<MyDebtsResponse.DebtSubItemResponse> subItems = new ArrayList<>();

        MyOwedToMeGroupBuilder(User fromUser) {
            this.fromUser = fromUser;
        }

        void addDebt(Debt debt) {
            if (debt != null && debt.getAmount() != null) {
                this.totalAmount = this.totalAmount.add(debt.getAmount());
                subItems.add(MyDebtsResponse.DebtSubItemResponse.builder()
                        .id(debt.getId())
                        .amount(debt.getAmount())
                        .expenseId(debt.getExpense() != null ? debt.getExpense().getId() : null)
                        .build());
            }
        }
    }
}
