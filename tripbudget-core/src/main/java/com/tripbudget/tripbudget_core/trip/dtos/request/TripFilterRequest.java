package com.tripbudget.tripbudget_core.trip.dtos.request;

public record TripFilterRequest(
        String search,
        String name,
        String destination,
        String currency,
        String status,
        String sortBy,
        String sortDirection
) {}

