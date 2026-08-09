package com.hcmut.divvy.service.model;

import com.hcmut.divvy.entity.enums.MediaEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAttachmentsModel {
    private MediaEntityType entityType;
    private Integer entityId;
}
