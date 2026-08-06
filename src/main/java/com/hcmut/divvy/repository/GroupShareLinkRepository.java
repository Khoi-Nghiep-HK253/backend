package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.GroupShareLink;
import com.hcmut.divvy.entity.enums.ShareLinkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupShareLinkRepository extends JpaRepository<GroupShareLink, Integer> {

    Optional<GroupShareLink> findByInviteCode(String inviteCode);

    List<GroupShareLink> findByGroupIdAndStatus(Integer groupId, ShareLinkStatus status);

    List<GroupShareLink> findByGroupId(Integer groupId);
}
