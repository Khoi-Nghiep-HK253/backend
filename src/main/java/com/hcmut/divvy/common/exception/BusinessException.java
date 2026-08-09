package com.hcmut.divvy.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * General-purpose business logic exception with configurable HTTP status.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * throw new BusinessException("Cat name already exists", HttpStatus.CONFLICT);
 * </pre>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
