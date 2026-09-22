package com.tripbudget.tripbudget_core.plan.dtos.request;

import com.tripbudget.tripbudget_core.plan.enums.ActivityCategory;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CreateActivityRequest(
        @NotBlank(message = "Title is required")
        String title,

        LocalTime startTime,

        LocalTime endTime,

        String location,

        ActivityCategory category,

        BigDecimal estimatedCost,

        Integer orderIndex,

        String note
) {}

