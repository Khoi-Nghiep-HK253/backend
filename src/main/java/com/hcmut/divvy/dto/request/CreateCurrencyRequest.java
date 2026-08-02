package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCurrencyRequest {

    @NotBlank(message = "Currency name is required")
    @Size(max = 50, message = "Currency name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Currency acronym is required")
    @Size(max = 10, message = "Currency acronym must not exceed 10 characters")
    private String acronym;
}
