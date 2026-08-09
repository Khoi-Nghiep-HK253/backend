package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DebtMapper {

        default GetGroupDebtsModel toGetGroupDebtsModel(Integer groupId, String currentUsername, DebtStatus status,
                        Integer userId) {
                return GetGroupDebtsModel.builder()
                                .groupId(groupId)
                                .currentUsername(currentUsername)
                                .status(status)
                                .userId(userId)
                                .build();
        }

        default GetGroupDebtSummaryModel toGetGroupDebtSummaryModel(Integer groupId, String currentUsername) {
                return GetGroupDebtSummaryModel.builder()
                                .groupId(groupId)
                                .currentUsername(currentUsername)
                                .build();
        }

        default GetMyDebtsModel toGetMyDebtsModel(Integer groupId, String currentUsername) {
                return GetMyDebtsModel.builder()
                                .groupId(groupId)
                                .currentUsername(currentUsername)
                                .build();
        }

        default GetDebtByIdModel toGetDebtByIdModel(Integer groupId, Integer debtId, String currentUsername) {
                return GetDebtByIdModel.builder()
                                .groupId(groupId)
                                .debtId(debtId)
                                .currentUsername(currentUsername)
                                .build();
        }

        default DebtUserInfoResponse toUserInfoResponse(User user) {
                if (user == null)
                        return null;
                String fullname = null;
                if (user.getLastname() != null || user.getFirstname() != null) {
                        fullname = ((user.getLastname() != null ? user.getLastname() : "") + " " +
                                        (user.getFirstname() != null ? user.getFirstname() : "")).trim();
                }
                return DebtUserInfoResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .fullname(fullname != null && !fullname.isEmpty() ? fullname : null)
                                .build();
        }

        default DebtItemResponse toDebtItemResponse(Debt debt) {
                if (debt == null)
                        return null;
                return DebtItemResponse.builder()
                                .id(debt.getId())
                                .expense(DebtItemResponse.ExpenseInfo.builder()
                                                .id(debt.getExpense() != null ? debt.getExpense().getId() : null)
                                                .description(debt.getExpense() != null
                                                                ? debt.getExpense().getDescription()
                                                                : null)
                                                .build())
                                .fromUser(toUserInfoResponse(debt.getFromUser()))
                                .toUser(toUserInfoResponse(debt.getToUser()))
                                .amount(debt.getAmount())
                                .status(debt.getStatus())
                                .createdAt(debt.getCreatedAt())
                                .build();
        }

        default DebtDetailResponse toDebtDetailResponse(Debt debt, List<Object> settlements) {
                if (debt == null)
                        return null;
                return DebtDetailResponse.builder()
                                .id(debt.getId())
                                .expense(DebtDetailResponse.ExpenseDetailInfo.builder()
                                                .id(debt.getExpense() != null ? debt.getExpense().getId() : null)
                                                .description(debt.getExpense() != null
                                                                ? debt.getExpense().getDescription()
                                                                : null)
                                                .expenseDate(debt.getExpense() != null
                                                                ? debt.getExpense().getExpenseDate()
                                                                : null)
                                                .build())
                                .fromUser(toUserInfoResponse(debt.getFromUser()))
                                .toUser(toUserInfoResponse(debt.getToUser()))
                                .amount(debt.getAmount())
                                .status(debt.getStatus())
                                .settlements(settlements != null ? settlements : List.of())
                                .createdAt(debt.getCreatedAt())
                                .build();
        }
}
