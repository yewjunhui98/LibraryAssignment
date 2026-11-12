package com.library.common;

import java.util.Locale;
import java.util.Optional;

public final class IsbnNormalizer {

    private IsbnNormalizer() {
    }

    /**
     * Returns the canonical ISBN-13, converting a valid ISBN-10 with the 978 prefix.
     */
    public static Optional<String> toIsbn13(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String compact = raw.replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
        if (compact.length() == 10 && isValidIsbn10(compact)) {
            return Optional.of(isbn10To13(compact));
        }
        if (compact.length() == 13 && compact.chars().allMatch(Character::isDigit) && isValidIsbn13(compact)) {
            return Optional.of(compact);
        }
        return Optional.empty();
    }

    private static boolean isValidIsbn10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char character = isbn.charAt(i);
            int digit;
            if (i == 9 && character == 'X') {
                digit = 10;
            } else if (Character.isDigit(character)) {
                digit = character - '0';
            } else {
                return false;
            }
            sum += digit * (10 - i);
        }
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }

    private static String isbn10To13(String isbn10) {
        String body = "978" + isbn10.substring(0, 9);
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = body.charAt(i) - '0';
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }
        int check = (10 - (sum % 10)) % 10;
        return body + check;
    }
}
