package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateExpenseRequest;
import com.hcmut.divvy.dto.request.UpdateExpenseRequest;
import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.entity.enums.SplitType;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    CreateExpenseModel toModel(CreateExpenseRequest request, Integer groupId, String currentUsername);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "expenseId", source = "expenseId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    UpdateExpenseModel toModel(UpdateExpenseRequest request, Integer groupId, Integer expenseId,
            String currentUsername);

    default GetGroupExpensesModel toGetGroupExpensesModel(Integer groupId, String currentUsername, LocalDate fromDate,
            LocalDate toDate, Pageable pageable) {
        return GetGroupExpensesModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .fromDate(fromDate)
                .toDate(toDate)
                .pageable(pageable)
                .build();
    }

    default GetExpenseByIdModel toGetExpenseByIdModel(Integer groupId, Integer expenseId, String currentUsername) {
        return GetExpenseByIdModel.builder()
                .groupId(groupId)
                .expenseId(expenseId)
                .currentUsername(currentUsername)
                .build();
    }

    default DeleteExpenseModel toDeleteExpenseModel(Integer groupId, Integer expenseId, String currentUsername) {
        return DeleteExpenseModel.builder()
                .groupId(groupId)
                .expenseId(expenseId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "group")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "description", source = "model.description")
    @Mapping(target = "totalAmount", source = "model.totalAmount")
    @Mapping(target = "splitType", source = "splitType")
    @Mapping(target = "expenseDate", source = "model.expenseDate")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Expense toEntity(CreateExpenseModel model, Group group, Currency currency, SplitType splitType, User createdBy);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "description", source = "model.description")
    @Mapping(target = "totalAmount", source = "model.totalAmount")
    @Mapping(target = "splitType", source = "splitType")
    @Mapping(target = "expenseDate", source = "model.expenseDate")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateExpenseModel model, Currency currency, SplitType splitType, @MappingTarget Expense expense);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expense", source = "expense")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExpensePayer toPayerEntity(Expense expense, User user, BigDecimal amount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expense", source = "expense")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExpenseShare toShareEntity(Expense expense, User user, BigDecimal amount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expense", source = "expense")
    @Mapping(target = "fromUser", source = "fromUser")
    @Mapping(target = "toUser", source = "toUser")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Debt toDebt(Expense expense, User fromUser, User toUser, BigDecimal amount, DebtStatus status);

    default ExpensePayerResponse toPayerResponse(ExpensePayer payer) {
        if (payer == null)
            return null;
        return ExpensePayerResponse.builder()
                .userId(payer.getUser().getId())
                .username(payer.getUser().getUsername())
                .amount(payer.getAmount())
                .build();
    }

    default ExpenseShareResponse toShareResponse(ExpenseShare share) {
        if (share == null)
            return null;
        return ExpenseShareResponse.builder()
                .userId(share.getUser().getId())
                .username(share.getUser().getUsername())
                .amount(share.getAmount())
                .build();
    }

    default DebtCreatedResponse toDebtResponse(Debt debt) {
        if (debt == null)
            return null;
        return DebtCreatedResponse.builder()
                .id(debt.getId())
                .fromUserId(debt.getFromUser().getId())
                .fromUsername(debt.getFromUser().getUsername())
                .toUserId(debt.getToUser().getId())
                .toUsername(debt.getToUser().getUsername())
                .amount(debt.getAmount())
                .status(debt.getStatus() != null ? debt.getStatus().name() : null)
                .build();
    }

    default ExpenseSummaryResponse toSummaryResponse(Expense expense, int payerCount, int shareCount,
            String createdByName) {
        if (expense == null)
            return null;
        return ExpenseSummaryResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .currency(toCurrencyResponse(expense.getCurrency()))
                .expenseDate(expense.getExpenseDate())
                .splitType(expense.getSplitType())
                .payerCount(payerCount)
                .shareCount(shareCount)
                .createdByName(createdByName)
                .build();
    }

    default ExpenseResponse toExpenseResponse(Expense expense, List<ExpensePayer> payers, List<ExpenseShare> shares,
            List<Debt> debts) {
        if (expense == null)
            return null;
        String createdByName = expense.getCreatedBy() != null ? expense.getCreatedBy().getUsername() : null;
        return ExpenseResponse.builder()
                .id(expense.getId())
                .group(toGroupResponse(expense.getGroup()))
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .currency(toCurrencyResponse(expense.getCurrency()))
                .expenseDate(expense.getExpenseDate())
                .splitType(expense.getSplitType())
                .createdByName(createdByName)
                .payers(payers != null ? payers.stream().map(this::toPayerResponse).toList() : List.of())
                .shares(shares != null ? shares.stream().map(this::toShareResponse).toList() : List.of())
                .debtsCreated(debts != null ? debts.stream().map(this::toDebtResponse).toList() : List.of())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    default GroupResponse toGroupResponse(Group group) {
        if (group == null)
            return null;
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }

    default CurrencyResponse toCurrencyResponse(Currency currency) {
        if (currency == null)
            return null;
        return CurrencyResponse.builder()
                .id(currency.getId())
                .code(currency.getAcronym())
                .name(currency.getName())
                .build();
    }
}
