package com.hcmut.divvy.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource cannot be found.
 * Maps to HTTP 404 Not Found.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * catRepository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException("Cat", "id", id));
 * </pre>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
