package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRoleRequest {

    @NotBlank(message = "role is required")
    @Pattern(regexp = "OWNER|MEMBER", message = "role must be OWNER or MEMBER")
    private String role;
}
