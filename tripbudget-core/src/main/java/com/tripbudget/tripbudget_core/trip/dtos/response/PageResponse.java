package com.tripbudget.tripbudget_core.trip.dtos.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        Pagination pagination
) {
    public static <T> PageResponse<T> from(Page<T> pageData) {
        Pagination pagination = new Pagination(
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.hasNext()
        );

        return new PageResponse<>(
                pageData.getContent(),
                pagination
        );
    }

    public record Pagination(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext
    ) {
    }
}