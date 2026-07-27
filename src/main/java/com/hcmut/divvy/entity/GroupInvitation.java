package com.hcmut.divvy.entity;

import com.hcmut.divvy.common.audit.BaseEntity;
import com.hcmut.divvy.entity.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invitations", indexes = {
    @Index(name = "idx_group_invitations_group", columnList = "group_id"),
    @Index(name = "idx_group_invitations_invitee", columnList = "invitee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GroupInvitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}

