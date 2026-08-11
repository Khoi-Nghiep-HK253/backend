package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.*;
import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.entity.enums.DebtStatus;
import com.hcmut.divvy.mapper.ExpenseMapper;
import com.hcmut.divvy.repository.*;
import com.hcmut.divvy.service.AnalyticsService;
import com.hcmut.divvy.service.model.GetAnalyticsModel;
import com.hcmut.divvy.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

        private final UserRepository userRepository;
        private final GroupMemberRepository groupMemberRepository;
        private final ExpenseRepository expenseRepository;
        private final ExpenseShareRepository expenseShareRepository;
        private final DebtRepository debtRepository;
        private final ExpenseMapper expenseMapper;
        private final UserValidator userValidator;

        @Override
        public AnalyticsSummaryResponse getAnalyticsSummary(GetAnalyticsModel model) {
                User caller = userValidator.validateUserExists(
                                userRepository.findByUsername(model.getCurrentUsername()), "username",
                                model.getCurrentUsername());

                List<Integer> userGroupIds;
                if (model.getGroupId() != null) {
                        userGroupIds = List.of(model.getGroupId());
                } else {
                        List<GroupMember> userMemberships = groupMemberRepository.findByUserId(caller.getId());
                        userGroupIds = userMemberships.stream()
                                        .map(m -> m.getGroup().getId())
                                        .distinct()
                                        .collect(Collectors.toList());
                }

                if (userGroupIds.isEmpty()) {
                        return AnalyticsSummaryResponse.builder()
                                        .totalPersonalShare(BigDecimal.ZERO)
                                        .totalGroupExpense(BigDecimal.ZERO)
                                        .totalOwedToUser(BigDecimal.ZERO)
                                        .totalUserOwes(BigDecimal.ZERO)
                                        .categoryStats(Collections.emptyList())
                                        .timeTrendStats(Collections.emptyList())
                                        .topExpenses(Collections.emptyList())
                                        .build();
                }

                // Fetch expenses belonging to candidate groups
                List<Expense> allGroupExpenses = new ArrayList<>();
                for (Integer gId : userGroupIds) {
                        allGroupExpenses.addAll(expenseRepository.findByGroupId(gId));
                }

                // Apply Date Filtering
                LocalDateTime start = model.getStartDate() != null ? model.getStartDate()
                                : LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime end = model.getEndDate() != null ? model.getEndDate() : LocalDateTime.now();

                List<Expense> filteredExpenses = allGroupExpenses.stream()
                                .filter(e -> {
                                        if (e.getExpenseDate() == null)
                                                return false;
                                        LocalDateTime expDateTime = e.getExpenseDate().atStartOfDay();
                                        return !expDateTime.isBefore(start) && !expDateTime.isAfter(end);
                                })
                                .collect(Collectors.toList());

                // 1. Total Group Expense
                BigDecimal totalGroupExpense = filteredExpenses.stream()
                                .map(e -> e.getTotalAmount() != null ? e.getTotalAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 2. Personal Shares Calculation
                Set<Integer> filteredExpenseIds = filteredExpenses.stream().map(Expense::getId)
                                .collect(Collectors.toSet());
                List<ExpenseShare> userShares = expenseShareRepository.findByUserId(caller.getId()).stream()
                                .filter(s -> filteredExpenseIds.contains(s.getExpense().getId()))
                                .collect(Collectors.toList());

                BigDecimal totalPersonalShare = userShares.stream()
                                .map(s -> s.getAmount() != null ? s.getAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 3. Debts (Owed To User & User Owes)
                List<Debt> pendingOwedToUser = debtRepository.findByToUserIdAndStatus(caller.getId(),
                                DebtStatus.PENDING);
                BigDecimal totalOwedToUser = pendingOwedToUser.stream()
                                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<Debt> pendingUserOwes = debtRepository.findByFromUserIdAndStatus(caller.getId(),
                                DebtStatus.PENDING);
                BigDecimal totalUserOwes = pendingUserOwes.stream()
                                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 4. Category Stats Grouping
                Map<Integer, CategoryExpenseStatResponse> catMap = new HashMap<>();
                for (Expense e : filteredExpenses) {
                        Category cat = e.getGroup() != null ? e.getGroup().getCategory() : null;
                        Integer cId = cat != null ? cat.getId() : 0;
                        String cName = cat != null ? cat.getName() : "Khác";
                        String cIcon = cat != null ? cat.getIcon() : "category";

                        CategoryExpenseStatResponse stat = catMap.computeIfAbsent(cId,
                                        k -> CategoryExpenseStatResponse.builder()
                                                        .categoryId(cId)
                                                        .categoryName(cName)
                                                        .categoryIcon(cIcon)
                                                        .totalAmount(BigDecimal.ZERO)
                                                        .expenseCount(0L)
                                                        .percentage(0.0)
                                                        .build());

                        stat.setTotalAmount(stat.getTotalAmount()
                                        .add(e.getTotalAmount() != null ? e.getTotalAmount() : BigDecimal.ZERO));
                        stat.setExpenseCount(stat.getExpenseCount() + 1);
                }

                List<CategoryExpenseStatResponse> categoryStats = new ArrayList<>(catMap.values());
                if (totalGroupExpense.compareTo(BigDecimal.ZERO) > 0) {
                        for (CategoryExpenseStatResponse stat : categoryStats) {
                                double pct = stat.getTotalAmount()
                                                .multiply(BigDecimal.valueOf(100))
                                                .divide(totalGroupExpense, 2, RoundingMode.HALF_UP)
                                                .doubleValue();
                                stat.setPercentage(pct);
                        }
                }
                categoryStats.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));

                // 5. Time Trend Grouping
                String groupBy = model.getGroupBy() != null ? model.getGroupBy().toUpperCase() : "DAY";
                Map<String, BigDecimal> timeMap = new LinkedHashMap<>();

                filteredExpenses.sort(Comparator.comparing(Expense::getExpenseDate));
                for (Expense e : filteredExpenses) {
                        String label;
                        if ("MONTH".equals(groupBy)) {
                                label = e.getExpenseDate().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                        } else if ("WEEK".equals(groupBy)) {
                                label = "Tuần " + e.getExpenseDate()
                                                .get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
                        } else {
                                label = e.getExpenseDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                        }
                        timeMap.merge(label, e.getTotalAmount() != null ? e.getTotalAmount() : BigDecimal.ZERO,
                                        BigDecimal::add);
                }

                List<TimePeriodStatResponse> timeTrendStats = timeMap.entrySet().stream()
                                .map(entry -> TimePeriodStatResponse.builder()
                                                .periodLabel(entry.getKey())
                                                .totalAmount(entry.getValue())
                                                .build())
                                .collect(Collectors.toList());

                // 6. Top 5 Expenses
                List<Expense> sortedTop = new ArrayList<>(filteredExpenses);
                sortedTop.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
                List<ExpenseResponse> topExpenses = sortedTop.stream()
                                .limit(5)
                                .map(e -> expenseMapper.toExpenseResponse(e, Collections.emptyList(),
                                                Collections.emptyList(), Collections.emptyList()))
                                .collect(Collectors.toList());

                return AnalyticsSummaryResponse.builder()
                                .totalPersonalShare(totalPersonalShare)
                                .totalGroupExpense(totalGroupExpense)
                                .totalOwedToUser(totalOwedToUser)
                                .totalUserOwes(totalUserOwes)
                                .categoryStats(categoryStats)
                                .timeTrendStats(timeTrendStats)
                                .topExpenses(topExpenses)
                                .build();
        }
}
