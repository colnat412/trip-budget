package com.tripbudget.tripbudget_core.common.dtos;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUserDto(
        Long id,
        String sessionId
) {
    public CurrentUserDto(Jwt jwt) {
        this(
                jwt.getSubject() != null ? Long.parseLong(jwt.getSubject()) : null,
                jwt.getClaimAsString("sid")
                // jwt.getClaimAsString("email"), jwt.getClaimAsString("role")
        );
    }
}