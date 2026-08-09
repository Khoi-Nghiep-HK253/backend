package com.hcmut.divvy.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.dto.response.MediaAttachmentResponse;
import com.hcmut.divvy.entity.MediaAttachment;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.MediaEntityType;
import com.hcmut.divvy.repository.MediaAttachmentRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.MediaAttachmentService;
import com.hcmut.divvy.service.model.DeleteMediaModel;
import com.hcmut.divvy.service.model.GetAttachmentsModel;
import com.hcmut.divvy.service.model.UploadMediaModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaAttachmentServiceImpl implements MediaAttachmentService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L; // 10 MB

    /** Cloudinary folder prefix for all Divvy uploads. */
    private static final Map<MediaEntityType, String> FOLDER_MAP = Map.of(
            MediaEntityType.EXPENSE,      "divvy/receipts",
            MediaEntityType.SETTLEMENT,   "divvy/proofs",
            MediaEntityType.USER_AVATAR,  "divvy/avatars/users",
            MediaEntityType.GROUP_AVATAR, "divvy/avatars/groups",
            MediaEntityType.GROUP_COVER,  "divvy/covers"
    );

    private final Cloudinary cloudinary;
    private final MediaAttachmentRepository mediaAttachmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MediaAttachmentResponse upload(UploadMediaModel model) {
        MultipartFile file = model.getFile();
        validateFile(file);

        User uploader = findUser(model.getCurrentUsername());
        String folder = FOLDER_MAP.getOrDefault(model.getEntityType(), "divvy/misc");

        Map<?, ?> uploadResult = uploadToCloudinary(file, folder);

        String fileUrl  = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");
        Object bytes    = uploadResult.get("bytes");

        MediaAttachment attachment = MediaAttachment.builder()
                .fileUrl(fileUrl)
                .publicId(publicId)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(bytes instanceof Number n ? n.longValue() : file.getSize())
                .entityType(model.getEntityType())
                .entityId(model.getEntityId())
                .uploader(uploader)
                .build();

        MediaAttachment saved = mediaAttachmentRepository.save(attachment);
        log.info("Uploaded media [{}] for entity [{}/{}] by user [{}]",
                publicId, model.getEntityType(), model.getEntityId(), model.getCurrentUsername());

        return toResponse(saved);
    }

    @Override
    public List<MediaAttachmentResponse> getAttachments(GetAttachmentsModel model) {
        return mediaAttachmentRepository
                .findByEntityTypeAndEntityId(model.getEntityType(), model.getEntityId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(DeleteMediaModel model) {
        MediaAttachment attachment = mediaAttachmentRepository.findById(model.getAttachmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MediaAttachment", "id", model.getAttachmentId()));

        // Only the uploader may delete their own file
        if (!attachment.getUploader().getUsername().equals(model.getCurrentUsername())) {
            throw new BusinessException(
                    "You are not allowed to delete this attachment", HttpStatus.FORBIDDEN);
        }

        deleteFromCloudinary(attachment.getPublicId());
        mediaAttachmentRepository.delete(attachment);

        log.info("Deleted media [{}] (attachmentId={}) by user [{}]",
                attachment.getPublicId(), model.getAttachmentId(), model.getCurrentUsername());
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Uploads a {@link MultipartFile} to Cloudinary and returns the raw result map.
     */
    private Map<?, ?> uploadToCloudinary(MultipartFile file, String folder) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );
        } catch (IOException e) {
            log.error("Cloudinary upload failed for file [{}]", file.getOriginalFilename(), e);
            throw new BusinessException("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Deletes an asset from Cloudinary by its {@code publicId}.
     * Errors are logged but do NOT throw so that the DB record can still be cleaned up.
     */
    private void deleteFromCloudinary(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            log.error("Failed to delete Cloudinary asset [{}]: {}", publicId, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Uploaded file must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException("File size exceeds the maximum allowed limit of 10 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Only image files are allowed (JPEG, PNG, WEBP, etc.)");
        }
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private MediaAttachmentResponse toResponse(MediaAttachment attachment) {
        return MediaAttachmentResponse.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .entityType(attachment.getEntityType())
                .entityId(attachment.getEntityId())
                .uploadedBy(attachment.getUploader().getUsername())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
