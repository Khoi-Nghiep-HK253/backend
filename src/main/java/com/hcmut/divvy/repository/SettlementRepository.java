package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
}
