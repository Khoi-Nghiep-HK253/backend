package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
    List<ExpenseShare> findByExpenseId(Integer expenseId);

    List<ExpenseShare> findByUserId(Integer userId);

    List<ExpenseShare> findByExpenseGroupId(Integer groupId);

    void deleteByExpenseId(Integer expenseId);
}
