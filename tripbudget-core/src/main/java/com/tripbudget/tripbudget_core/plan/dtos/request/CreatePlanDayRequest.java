package com.tripbudget.tripbudget_core.plan.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreatePlanDayRequest(
        @NotNull(message = "Day number is required")
        @Positive(message = "Day number must be greater than 0")
        Integer dayNumber,

        LocalDate planDate,

        String title,

        String note
) {}

