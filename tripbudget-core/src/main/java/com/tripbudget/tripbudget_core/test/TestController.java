package com.tripbudget.tripbudget_core.test;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ResponseEntity<Map<String, String>> testJwt(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                Map.of(
                        "message", "JWT is valid. You can access Travel Core.",
                        "currentUserId", jwt.getSubject(),
                        "sessionId", jwt.getClaimAsString("sid")
                )
        );
    }
}