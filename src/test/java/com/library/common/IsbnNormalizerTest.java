package com.library.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsbnNormalizerTest {

    @Test
    void acceptsIsbn13WithHyphens() {
        assertEquals("9780134685991", IsbnNormalizer.toIsbn13("978-0-13-468599-1").orElseThrow());
    }

    @Test
    void convertsIsbn10ToIsbn13() {
        assertEquals("9780134685991", IsbnNormalizer.toIsbn13("0-13-468599-7").orElseThrow());
    }

    @Test
    void acceptsIsbn10CheckDigitX() {
        assertTrue(IsbnNormalizer.toIsbn13("0-8044-2957-X").isPresent());
    }

    @Test
    void rejectsBadChecksum() {
        assertTrue(IsbnNormalizer.toIsbn13("9780134685992").isEmpty());
    }

    @Test
    void rejectsBlankAndGarbage() {
        assertTrue(IsbnNormalizer.toIsbn13(" ").isEmpty());
        assertTrue(IsbnNormalizer.toIsbn13("not-an-isbn").isEmpty());
    }
}
