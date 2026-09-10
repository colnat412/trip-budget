package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull(message = "Role cannot be null")
        TripMemberRole role
) {}

