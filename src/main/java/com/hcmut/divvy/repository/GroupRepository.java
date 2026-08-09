package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT g FROM Group g JOIN GroupMember gm ON gm.group = g WHERE gm.user.id = :userId")
    Page<Group> findAllByMemberId(@Param("userId") Integer userId, Pageable pageable);
}
