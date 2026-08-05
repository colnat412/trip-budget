package com.tripbudget.tripbudget_core.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RedisSessionValidationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSessionValidationFilter(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String userId = jwtAuth.getToken().getSubject();
            String sessionId = jwtAuth.getToken().getClaimAsString("sid");
            String tokenType = jwtAuth.getToken().getClaimAsString("type");

            if (!"access".equals(tokenType) || sessionId == null) {
                reject(response);
                return;
            }

            String sessionRaw = redisTemplate
                    .opsForValue()
                    .get("auth:session:" + sessionId);

            if (sessionRaw == null) {
                reject(response);
                return;
            }

//            try {
//                JsonNode session = objectMapper.readTree(sessionRaw);
//                String sessionUserId = session.path("userId").asText();
//
//                if (!userId.equals(sessionUserId)) {
//                    reject(response);
//                    return;
//                }
//            } catch (JsonProcessingException error) {
//                reject(response);
//                return;
//            }
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Session expired or revoked"
        );
    }
}