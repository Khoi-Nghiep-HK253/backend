package com.hcmut.divvy.entity;

import com.hcmut.divvy.entity.enums.MediaEntityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Stores metadata of a file uploaded to Cloudinary and associated with a domain
 * entity.
 *
 * <p>
 * This is a generic, polymorphic attachment table: one row = one file.
 * The {@code entity_type + entity_id} pair identifies which domain object owns
 * this attachment.
 * Examples:
 * <ul>
 * <li>entityType=EXPENSE, entityId=42 → receipt image for Expense #42</li>
 * <li>entityType=SETTLEMENT, entityId=7 → bank-transfer proof for Settlement
 * #7</li>
 * <li>entityType=USER_AVATAR, entityId=1 → avatar photo for User #1</li>
 * <li>entityType=GROUP_COVER, entityId=3 → cover banner for Group #3</li>
 * </ul>
 */
@Entity
@Table(name = "media_attachments", indexes = {
        @Index(name = "idx_media_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_media_uploader", columnList = "uploader_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Secure CDN URL returned by Cloudinary (e.g. https://res.cloudinary.com/...).
     */
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    /**
     * Cloudinary public ID used when deleting the asset.
     * Example: "divvy/receipts/bill_abc123"
     */
    @Column(name = "public_id", nullable = false, length = 255)
    private String publicId;

    /** Original filename provided by the client (for display purposes only). */
    @Column(name = "file_name", length = 255)
    private String fileName;

    /** MIME type of the uploaded file (e.g. "image/jpeg", "image/png"). */
    @Column(name = "file_type", length = 50)
    private String fileType;

    /** Size of the file in bytes. */
    @Column(name = "file_size")
    private Long fileSize;

    /** The domain entity type this attachment belongs to. */
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 30)
    private MediaEntityType entityType;

    /** The primary key of the domain entity this attachment belongs to. */
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    /** The user who uploaded this file. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
