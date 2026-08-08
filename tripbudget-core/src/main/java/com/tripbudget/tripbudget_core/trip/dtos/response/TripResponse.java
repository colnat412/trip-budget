package com.tripbudget.tripbudget_core.trip.dtos.response;

import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripStatus;

import java.time.Instant;
import java.time.LocalDate;

// record is readonly
public record TripResponse(
        Long id,
        Long ownerId,
        String name,
        String destination,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String baseCurrency,
        TripStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
    // from: convert entity to response
    public static TripResponse from(TripEntity trip) {
        return new TripResponse(
                // hashIdService.encode(trip.getId()),
                trip.getId(),
                trip.getOwnerId(),
                trip.getName(),
                trip.getDestination(),
                trip.getDescription(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getBaseCurrency(),
                trip.getStatus(),
                trip.getVersion(),
                trip.getCreatedAt(),
                trip.getUpdatedAt()
        );
    }
}