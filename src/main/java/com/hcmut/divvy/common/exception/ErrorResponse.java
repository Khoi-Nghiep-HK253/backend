package com.hcmut.divvy.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Structured error response body returned by {@link GlobalExceptionHandler}.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;

    /** Field-level validation errors. Only present for 400 Validation failures. */
    private final Map<String, String> errors;
}
