package com.tripbudget.tripbudget_core.trip.dtos.response;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripStatus;

import java.time.Instant;
import java.time.LocalDate;

// record is readonly
public record TripResponse(
        String id,
        String ownerId,
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
    // from: convert entity to response with encoded IDs
    public static TripResponse from(TripEntity trip, HashidsService hashidsService) {
        return new TripResponse(
                hashidsService != null ? hashidsService.encode(trip.getId()) : String.valueOf(trip.getId()),
                hashidsService != null ? hashidsService.encode(trip.getOwnerId()) : String.valueOf(trip.getOwnerId()),
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