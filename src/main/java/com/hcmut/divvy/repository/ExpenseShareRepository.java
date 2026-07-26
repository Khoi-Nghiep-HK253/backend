package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
}
