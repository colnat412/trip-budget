package com.tripbudget.tripbudget_core.trip.dtos.response;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;

public record PublicTripViewerResponse(
        TripMemberStatus currentUserStatus
) {}
