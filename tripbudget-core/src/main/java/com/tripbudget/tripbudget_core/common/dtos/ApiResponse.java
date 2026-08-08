package com.tripbudget.tripbudget_core.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return new ApiResponse<>(
                status.value(),
                message,
                data
        );
    }

    public static ApiResponse<Void> error(
            HttpStatus status,
            String message
    ) {
        return new ApiResponse<>(
                status.value(),
                message,
                null
        );
    }
}