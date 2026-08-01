package com.hcmut.divvy.helper;

import java.security.SecureRandom;

/**
 * Utility methods for common String operations across the application.
 */
public final class StringHelper {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private StringHelper() {}

    /**
     * Masks an email address for safe display.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "hung@example.com"} → {@code "h***@example.com"}</li>
     *   <li>{@code "a@example.com"}   → {@code "***@example.com"}</li>
     * </ul>
     *
     * @param email the raw email address
     * @return a masked version of the email
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) return "";
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return "***" + email.substring(atIndex);
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length length of the string to generate
     * @return random alphanumeric string
     */
    public static String generateRandomAlphanumeric(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
