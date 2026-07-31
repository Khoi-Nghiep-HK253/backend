package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberRequest {

    @NotNull(message = "userId is required")
    private Integer userId;
}
