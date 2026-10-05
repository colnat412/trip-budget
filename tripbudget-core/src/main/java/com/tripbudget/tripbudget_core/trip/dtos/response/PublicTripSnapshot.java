package com.tripbudget.tripbudget_core.trip.dtos.response;

import com.tripbudget.tripbudget_core.plan.dtos.response.TripPlanOverviewResponse;

import java.time.Instant;

public record PublicTripSnapshot(
        PublicTripResponse trip,
        TripPlanOverviewResponse plan,
        Instant generatedAt
) {}
