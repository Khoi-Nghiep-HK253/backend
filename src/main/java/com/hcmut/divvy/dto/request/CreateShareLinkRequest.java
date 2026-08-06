package com.hcmut.divvy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShareLinkRequest {

    /** Expiration in hours (e.g. 24, 168 for 7 days, 720 for 30 days). Null or 0 means never expires. */
    private Integer expireHours;

    /** Max number of uses allowed. Null or <= 0 means unlimited. */
    private Integer maxUses;
}
