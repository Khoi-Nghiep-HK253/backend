package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    boolean existsByGroupIdAndUserId(Integer groupId, Integer userId);

    Optional<GroupMember> findByGroupIdAndUserId(Integer groupId, Integer userId);
}
