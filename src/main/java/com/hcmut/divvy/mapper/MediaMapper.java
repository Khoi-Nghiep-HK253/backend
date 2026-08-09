package com.hcmut.divvy.mapper;

import com.hcmut.divvy.entity.enums.MediaEntityType;
import com.hcmut.divvy.service.model.DeleteMediaModel;
import com.hcmut.divvy.service.model.GetAttachmentsModel;
import com.hcmut.divvy.service.model.UploadMediaModel;
import org.mapstruct.Mapper;
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
}
