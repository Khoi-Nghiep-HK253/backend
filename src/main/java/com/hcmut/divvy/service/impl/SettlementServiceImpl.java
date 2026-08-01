package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.SettlementDetailResponse;
import com.hcmut.divvy.dto.response.SettlementResponse;
import com.hcmut.divvy.dto.response.SettlementSummaryResponse;
import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.mapper.SettlementMapper;
import com.hcmut.divvy.repository.*;
import com.hcmut.divvy.service.SettlementService;
import com.hcmut.divvy.service.model.CreateSettlementModel;
import com.hcmut.divvy.service.model.GetGroupSettlementsModel;
import com.hcmut.divvy.service.model.GetSettlementByIdModel;
import com.hcmut.divvy.validator.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementServiceImpl implements SettlementService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final DebtRepository debtRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final ExpenseValidator expenseValidator;
    private final DebtValidator debtValidator;
    private final SettlementValidator settlementValidator;

    @Override
    @Transactional
    public SettlementResponse create(CreateSettlementModel model) {
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
        settlementValidator.validateDebtForSettlement(debt, model.getAmount(), caller.getId());

        String method = (model.getMethod() != null && !model.getMethod().isBlank()) ? model.getMethod() : "CASH";
        LocalDateTime paidAt = model.getPaidAt() != null ? model.getPaidAt() : LocalDateTime.now();

        Settlement settlement = Settlement.builder()
                .debt(debt)
                .group(group)
                .fromUser(debt.getFromUser())
                .toUser(debt.getToUser())
                .amount(model.getAmount())
                .method(method)
                .note(model.getNote())
                .paidAt(paidAt)
                .build();

        Settlement savedSettlement = settlementRepository.save(settlement);

        // Update debt status to SETTLED
        debt.setStatus(DebtStatus.SETTLED);
        debtRepository.save(debt);

        return settlementMapper.toSettlementResponse(savedSettlement, debt);
    }

    @Override
    public Page<SettlementSummaryResponse> getGroupSettlements(GetGroupSettlementsModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Specification<Settlement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("group").get("id"), group.getId()));

            if (model.getFromUserId() != null) {
                predicates.add(cb.equal(root.get("fromUser").get("id"), model.getFromUserId()));
            }
            if (model.getToUserId() != null) {
                predicates.add(cb.equal(root.get("toUser").get("id"), model.getToUserId()));
            }
            if (model.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("paidAt"), model.getFromDate().atStartOfDay()));
            }
            if (model.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paidAt"), model.getToDate().atTime(LocalTime.MAX)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Settlement> settlementPage = settlementRepository.findAll(spec, model.getPageable());
        return settlementPage.map(settlementMapper::toSettlementSummaryResponse);
    }

    @Override
    public SettlementDetailResponse findById(GetSettlementByIdModel model) {
        User caller = userValidator.validateUserExists(
                userRepository.findByUsername(model.getCurrentUsername()), "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(
                groupRepository.findById(model.getGroupId()), model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        Settlement settlement = settlementValidator.validateSettlementExists(
                settlementRepository.findById(model.getSettlementId()), model.getSettlementId());
        settlementValidator.validateSettlementBelongsToGroup(settlement, group.getId());

        return settlementMapper.toSettlementDetailResponse(settlement);
    }
}
