package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPreviewResponse {
    private Integer groupId;
    private String groupName;
    private String categoryName;
    private String categoryIcon;
    private String note;
    private String createdByUsername;
    private Integer memberCount;
    private String inviteCode;
    private Boolean isValid;
    private String invalidReason;
}
