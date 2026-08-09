package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.MediaEntityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO returned after uploading or fetching media attachments.
 */
@Getter
@Builder
public class MediaAttachmentResponse {

    private Integer id;

    /** Publicly accessible Cloudinary CDN URL. */
    private String fileUrl;

    /** Original file name from the client. */
    private String fileName;

    /** MIME type (e.g. "image/jpeg"). */
    private String fileType;

    /** File size in bytes. */
    private Long fileSize;

    /** Domain entity type this attachment belongs to. */
    private MediaEntityType entityType;

    /** Primary key of the domain entity this attachment belongs to. */
    private Integer entityId;

    /** Username of the person who uploaded this file. */
    private String uploadedBy;

    private LocalDateTime createdAt;
}
