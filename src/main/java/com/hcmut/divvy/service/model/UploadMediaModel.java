package com.hcmut.divvy.service.model;

import com.hcmut.divvy.entity.enums.MediaEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadMediaModel {
    private MultipartFile file;
    private MediaEntityType entityType;
    private Integer entityId;
    private String currentUsername;
}
