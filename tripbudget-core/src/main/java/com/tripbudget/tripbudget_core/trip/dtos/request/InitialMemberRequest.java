package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InitialMemberRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotNull(message = "Role is required")
        TripMemberRole role
) {
}