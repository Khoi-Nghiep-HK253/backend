package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.response.MediaAttachmentResponse;
import com.hcmut.divvy.entity.enums.MediaEntityType;
import com.hcmut.divvy.mapper.MediaMapper;
import com.hcmut.divvy.service.MediaAttachmentService;
import com.hcmut.divvy.service.model.DeleteMediaModel;
import com.hcmut.divvy.service.model.GetAttachmentsModel;
import com.hcmut.divvy.service.model.UploadMediaModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for uploading, retrieving, and deleting media attachments.
 *
 * <p>
 * All endpoints require an authenticated user (JWT).
 * The upload endpoint streams the file directly to Cloudinary CDN and persists
 * the returned secure URL together with metadata in the
 * {@code media_attachments} table.
 */
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Attachments", description = "APIs for uploading and managing media files (receipts, proofs, avatars)")
public class MediaController {

    private final MediaAttachmentService mediaAttachmentService;
    private final MediaMapper mediaMapper;

    /**
     * Upload a file and attach it to a domain entity.
     *
     * <p>
     * The file is stored in Cloudinary under the folder matching
     * {@code entityType}:
     * <ul>
     * <li>EXPENSE → divvy/receipts</li>
     * <li>SETTLEMENT → divvy/proofs</li>
     * <li>USER_AVATAR → divvy/avatars/users</li>
     * <li>GROUP_AVATAR → divvy/avatars/groups</li>
     * <li>GROUP_COVER → divvy/covers</li>
     * </ul>
     *
     * @param file       the image file to upload (max 10 MB)
     * @param entityType the domain entity type
     * @param entityId   the primary key of the domain entity
     * @return 201 Created with the attachment metadata
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a media file and attach it to a domain entity")
    public ResponseEntity<ApiResponse<MediaAttachmentResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") MediaEntityType entityType,
            @RequestParam("entityId") Integer entityId,
            Authentication authentication) {

        UploadMediaModel model = mediaMapper.toUploadModel(file, entityType, entityId, authentication.getName());
        MediaAttachmentResponse response = mediaAttachmentService.upload(model);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "File uploaded successfully"));
    }

    /**
     * Retrieve all attachments linked to a given domain entity.
     *
     * @param entityType the type of domain entity (e.g. EXPENSE)
     * @param entityId   the primary key of the entity
     * @return 200 OK with the list of attachment metadata
     */
    @GetMapping("/attachments")
    @Operation(summary = "Get all media attachments for a specific entity")
    public ResponseEntity<ApiResponse<List<MediaAttachmentResponse>>> getAttachments(
            @RequestParam("entityType") MediaEntityType entityType,
            @RequestParam("entityId") Integer entityId) {

        GetAttachmentsModel model = mediaMapper.toGetAttachmentsModel(entityType, entityId);
        List<MediaAttachmentResponse> attachments = mediaAttachmentService.getAttachments(model);

        return ResponseEntity.ok(ApiResponse.ok(attachments, "Attachments retrieved successfully"));
    }

    /**
     * Delete an attachment by ID.
     *
     * <p>
     * Only the user who originally uploaded the file may delete it.
     * This removes both the Cloudinary asset and the DB record.
     *
     * @param id the primary key of the attachment
     * @return 200 OK on success; 403 if the caller is not the uploader; 404 if not
     *         found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a media attachment (only the uploader may delete)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id,
            Authentication authentication) {

        DeleteMediaModel model = mediaMapper.toDeleteModel(id, authentication.getName());
        mediaAttachmentService.delete(model);

        return ResponseEntity.ok(ApiResponse.ok(null, "Attachment deleted successfully"));
    }
}
