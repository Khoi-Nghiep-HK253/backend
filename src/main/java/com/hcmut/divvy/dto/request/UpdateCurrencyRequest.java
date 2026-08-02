package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCurrencyRequest {

    @Size(max = 50, message = "Currency name must not exceed 50 characters")
    private String name;

    @Size(max = 10, message = "Currency acronym must not exceed 10 characters")
    private String acronym;
}
