package com.hcmut.divvy.dto.response;

import com.hcmut.divvy.entity.enums.ShareLinkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareLinkResponse {
    private Integer id;
    private Integer groupId;
    private String groupName;
    private String inviteCode;
    private String createdByUsername;
    private Integer maxUses;
    private Integer usedCount;
    private LocalDateTime expiresAt;
    private ShareLinkStatus status;
    private LocalDateTime createdAt;
}
