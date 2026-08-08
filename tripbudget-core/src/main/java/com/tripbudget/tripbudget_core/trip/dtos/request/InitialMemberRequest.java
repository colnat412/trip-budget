package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InitialMemberRequest(

        @NotNull
        @Positive
        Long userId,

        @NotNull
        TripMemberRole role
) {
}