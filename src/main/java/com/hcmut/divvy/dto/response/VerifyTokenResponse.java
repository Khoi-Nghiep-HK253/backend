package com.hcmut.divvy.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyTokenResponse {

    /** Partially masked email, e.g. "h***@example.com" */
    private String email;

    private LocalDateTime expiresAt;
}
