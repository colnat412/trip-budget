package com.tripbudget.tripbudget_core.plan.dtos.request;

import com.tripbudget.tripbudget_core.plan.enums.ActivityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateActivityStatusRequest(
        @NotNull(message = "Status is required")
        ActivityStatus status
) {}

