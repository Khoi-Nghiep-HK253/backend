package com.hcmut.divvy.helper;

import java.util.UUID;

/**
 * Utility methods for generating secure tokens used in the application
 * (e.g., password reset, email verification).
 */
public final class TokenHelper {

    private TokenHelper() {}

    /**
     * Generates a compact, URL-safe UUID token (32 hex characters, no dashes).
     *
     * <p>Example output: {@code "a3f9b2c1d4e5f6a7b8c9d0e1f2a3b4c5"}
     *
     * @return a 32-character alphanumeric token string
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
