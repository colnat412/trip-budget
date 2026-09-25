package com.tripbudget.tripbudget_core.trip.dtos.response;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import com.tripbudget.tripbudget_core.trip.enums.TripVisibility;

public record TripShareResponse(
        String tripId,
        TripVisibility visibility,
        TripMemberRole publicRole,
        String shareToken
) {}
