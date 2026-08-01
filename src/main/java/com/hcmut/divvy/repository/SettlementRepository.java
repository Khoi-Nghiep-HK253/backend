package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer>, JpaSpecificationExecutor<Settlement> {
    List<Settlement> findByDebtId(Integer debtId);
}
