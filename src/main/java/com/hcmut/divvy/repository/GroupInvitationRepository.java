package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.GroupInvitation;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Integer> {

    boolean existsByGroupIdAndInviteeIdAndStatus(Integer groupId, Integer inviteeId, InvitationStatus status);

    List<GroupInvitation> findAllByGroupId(Integer groupId);

    List<GroupInvitation> findAllByGroupIdAndStatus(Integer groupId, InvitationStatus status);

    List<GroupInvitation> findAllByInviteeId(Integer inviteeId);

    List<GroupInvitation> findAllByInviteeIdAndStatus(Integer inviteeId, InvitationStatus status);

    Optional<GroupInvitation> findByToken(String token);
}
