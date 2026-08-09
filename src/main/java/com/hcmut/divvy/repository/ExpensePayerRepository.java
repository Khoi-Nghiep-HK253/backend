package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.ExpensePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpensePayerRepository extends JpaRepository<ExpensePayer, Integer> {
    List<ExpensePayer> findByExpenseId(Integer expenseId);

    void deleteByExpenseId(Integer expenseId);
}
