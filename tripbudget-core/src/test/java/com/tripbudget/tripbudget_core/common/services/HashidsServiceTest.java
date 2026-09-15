package com.tripbudget.tripbudget_core.common.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class HashidsServiceTest {

    private HashidsService hashidsService;

    @BeforeEach
    void setUp() {
        hashidsService = new HashidsService("6d8a1d503a90486cd6cef4fb216faabf", 8);
    }

    @Test
    void testEncodeAndDecodeRoundTrip() {
        Long originalId = 12345L;
        String encoded = hashidsService.encode(originalId);

        assertNotNull(encoded);
        assertTrue(encoded.length() >= 8);

        Long decoded = hashidsService.decode(encoded);
        assertEquals(originalId, decoded);
    }

    @Test
    void testEncodeSmallNumbers() {
        for (long i = 1; i <= 20; i++) {
            String hash = hashidsService.encode(i);
            assertNotNull(hash);
            assertTrue(hash.length() >= 8);
            assertEquals(i, hashidsService.decode(hash));
        }
    }

    @Test
    void testEncodeNullReturnsNull() {
        assertNull(hashidsService.encode(null));
    }

    @Test
    void testDecodeFallbackForNumericString() {
        // Migration fallback allows pure numeric string
        Long decoded = hashidsService.decode("9999");
        assertEquals(9999L, decoded);
    }

    @Test
    void testDecodeInvalidHashThrowsBadRequest() {
        assertThrows(ResponseStatusException.class, () -> {
            hashidsService.decode("invalid_hash_@!#");
        });
    }

    @Test
    void testDecodeBlankThrowsBadRequest() {
        assertThrows(ResponseStatusException.class, () -> {
            hashidsService.decode("   ");
        });
        assertThrows(ResponseStatusException.class, () -> {
            hashidsService.decode(null);
        });
    }

    @Test
    void testDecodeOrNull() {
        assertNull(hashidsService.decodeOrNull(null));
        assertNull(hashidsService.decodeOrNull(""));
        assertNull(hashidsService.decodeOrNull("invalid_tampered_hash_XYZ!"));

        String validHash = hashidsService.encode(42L);
        assertEquals(42L, hashidsService.decodeOrNull(validHash));
    }
}

