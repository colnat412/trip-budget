package com.tripbudget.tripbudget_core.common.services;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service providing bidirectional obfuscation between Long database IDs and URL-safe Hashid strings.
 * Reads HASHIDS_SALT from environment variables (.env).
 */
@Service
public class HashidsService {

    private final Hashids hashids;

    public HashidsService(
            @Value("${hashids.salt:${HASHIDS_SALT:6d8a1d503a90486cd6cef4fb216faabf}}") String salt,
            @Value("${hashids.min-length:${HASHIDS_MIN_LENGTH:8}}") int minLength
    ) {
        this.hashids = new Hashids(salt, minLength);
    }

    /**
     * Encodes a database Long ID into a URL-safe Hashid string.
     * Returns null if input is null.
     */
    public String encode(Long id) {
        if (id == null) {
            return null;
        }
        return this.hashids.encode(id);
    }

    /**
     * Decodes a Hashid string into the original Long database ID.
     * Throws 400 BAD_REQUEST if the hash is invalid or tampered with.
     * Supports graceful fallback for plain numeric IDs during client migration.
     */
    public Long decode(String hashId) {
        if (hashId == null || hashId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID must not be empty");
        }

        String trimmed = hashId.trim();

        // 1. Attempt Hashids decoding
        long[] decoded = this.hashids.decode(trimmed);
        if (decoded != null && decoded.length > 0) {
            return decoded[0];
        }

        // 2. Graceful migration fallback: If input is pure numeric digits, parse directly
        if (trimmed.matches("^\\d+$")) {
            try {
                return Long.parseLong(trimmed);
            } catch (NumberFormatException ignored) {
                // Fall through to error
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format: " + hashId);
    }

    /**
     * Safely decodes a Hashid string to Long, returning null if invalid or blank.
     */
    public Long decodeOrNull(String hashId) {
        if (hashId == null || hashId.isBlank()) {
            return null;
        }
        try {
            return decode(hashId);
        } catch (Exception e) {
            return null;
        }
    }
}

