package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateSettlementRequest;
import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface SettlementMapper {

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "currentUsername", source = "currentUsername")
    CreateSettlementModel toModel(CreateSettlementRequest request, Integer groupId, String currentUsername);

    default GetGroupSettlementsModel toGetGroupSettlementsModel(Integer groupId, String currentUsername, Integer fromUserId, Integer toUserId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return GetGroupSettlementsModel.builder()
                .groupId(groupId)
                .currentUsername(currentUsername)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .fromDate(fromDate)
                .toDate(toDate)
                .pageable(pageable)
                .build();
    }

    default GetSettlementByIdModel toGetSettlementByIdModel(Integer groupId, Integer settlementId, String currentUsername) {
        return GetSettlementByIdModel.builder()
                .groupId(groupId)
                .settlementId(settlementId)
                .currentUsername(currentUsername)
                .build();
    }

    default SettlementResponse toSettlementResponse(Settlement settlement, Debt debt) {
        if (settlement == null) return null;
        return SettlementResponse.builder()
                .id(settlement.getId())
                .debt(SettlementResponse.DebtStatusInfo.builder()
                        .id(debt != null ? debt.getId() : null)
                        .newStatus(debt != null && debt.getStatus() != null ? debt.getStatus().name() : null)
                        .build())
                .fromUser(toUserInfoResponse(settlement.getFromUser()))
                .toUser(toUserInfoResponse(settlement.getToUser()))
                .amount(settlement.getAmount())
                .method(settlement.getMethod())
                .note(settlement.getNote())
                .paidAt(settlement.getPaidAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }

    default SettlementSummaryResponse toSettlementSummaryResponse(Settlement settlement) {
        if (settlement == null) return null;
        return SettlementSummaryResponse.builder()
                .id(settlement.getId())
                .fromUser(toUserInfoResponse(settlement.getFromUser()))
                .toUser(toUserInfoResponse(settlement.getToUser()))
                .amount(settlement.getAmount())
                .method(settlement.getMethod())
                .paidAt(settlement.getPaidAt())
                .build();
    }

    default SettlementDetailResponse toSettlementDetailResponse(Settlement settlement) {
        if (settlement == null) return null;
        return SettlementDetailResponse.builder()
                .id(settlement.getId())
                .debt(SettlementDetailResponse.DebtInfo.builder()
                        .id(settlement.getDebt() != null ? settlement.getDebt().getId() : null)
                        .amount(settlement.getDebt() != null ? settlement.getDebt().getAmount() : null)
                        .build())
                .group(toGroupResponse(settlement.getGroup()))
                .fromUser(toUserInfoResponse(settlement.getFromUser()))
                .toUser(toUserInfoResponse(settlement.getToUser()))
                .amount(settlement.getAmount())
                .method(settlement.getMethod())
                .note(settlement.getNote())
                .paidAt(settlement.getPaidAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }

    default DebtUserInfoResponse toUserInfoResponse(User user) {
        if (user == null) return null;
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

    default GroupResponse toGroupResponse(Group group) {
        if (group == null) return null;
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }
}
