package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.response.MediaAttachmentResponse;
import com.hcmut.divvy.entity.MediaAttachment;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.entity.enums.MediaEntityType;
import com.hcmut.divvy.service.model.DeleteMediaModel;
import com.hcmut.divvy.service.model.GetAttachmentsModel;
import com.hcmut.divvy.service.model.SelectMediaModel;
import com.hcmut.divvy.service.model.UploadMediaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    default UploadMediaModel toUploadModel(MultipartFile file, MediaEntityType entityType,
            Integer entityId, String currentUsername) {
        return UploadMediaModel.builder()
                .file(file)
                .entityType(entityType)
                .entityId(entityId)
                .currentUsername(currentUsername)
                .build();
    }

    default GetAttachmentsModel toGetAttachmentsModel(MediaEntityType entityType, Integer entityId) {
        return GetAttachmentsModel.builder()
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }

    default DeleteMediaModel toDeleteModel(Integer attachmentId, String currentUsername) {
        return DeleteMediaModel.builder()
                .attachmentId(attachmentId)
                .currentUsername(currentUsername)
                .build();
    }

    default SelectMediaModel toSelectModel(Integer attachmentId, String currentUsername) {
        return SelectMediaModel.builder()
                .attachmentId(attachmentId)
                .currentUsername(currentUsername)
                .build();
    }

    @Mapping(target = "uploadedBy", source = "uploader.username")
    MediaAttachmentResponse toResponse(MediaAttachment attachment);

    default MediaAttachment toActiveAttachment(MediaAttachment existing, User uploader) {
        return MediaAttachment.builder()
                .fileUrl(existing.getFileUrl())
                .publicId(existing.getPublicId())
                .fileName(existing.getFileName())
                .fileType(existing.getFileType())
                .fileSize(existing.getFileSize())
                .entityType(existing.getEntityType())
                .entityId(existing.getEntityId())
                .uploader(uploader)
                .build();
    }

    default MediaAttachment toAttachment(
            UploadMediaModel model,
            User uploader,
            String fileUrl,
            String publicId,
            Long fileSize) {
        return MediaAttachment.builder()
                .fileUrl(fileUrl)
                .publicId(publicId)
                .fileName(model.getFile().getOriginalFilename())
                .fileType(model.getFile().getContentType())
                .fileSize(fileSize != null ? fileSize : model.getFile().getSize())
                .entityType(model.getEntityType())
                .entityId(model.getEntityId())
                .uploader(uploader)
                .build();
    }
}
