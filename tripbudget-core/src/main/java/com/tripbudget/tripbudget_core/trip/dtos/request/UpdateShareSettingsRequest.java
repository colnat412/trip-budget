package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import com.tripbudget.tripbudget_core.trip.enums.TripVisibility;
import jakarta.validation.constraints.NotNull;

public record UpdateShareSettingsRequest(
        @NotNull(message = "Visibility is required")
        TripVisibility visibility,

        TripMemberRole publicRole
) {}
