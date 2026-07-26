package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.ExpensePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensePayerRepository extends JpaRepository<ExpensePayer, Integer> {
}
