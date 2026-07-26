package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Integer> {
}
