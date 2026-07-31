package com.hcmut.divvy.helper;

/**
 * Utility methods for common String operations across the application.
 */
public final class StringHelper {

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
}
