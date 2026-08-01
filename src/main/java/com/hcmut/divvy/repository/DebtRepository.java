package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.Debt;
import com.hcmut.divvy.entity.enums.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer>, JpaSpecificationExecutor<Debt> {
    List<Debt> findByExpenseId(Integer expenseId);
    void deleteByExpenseId(Integer expenseId);
    boolean existsByExpenseIdAndStatusNot(Integer expenseId, DebtStatus status);
    List<Debt> findByExpenseGroupId(Integer groupId);
    List<Debt> findByExpenseGroupIdAndStatus(Integer groupId, DebtStatus status);
}
