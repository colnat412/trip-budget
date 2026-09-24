package com.tripbudget.tripbudget_core.plan.dtos.request;

import com.tripbudget.tripbudget_core.plan.enums.ActivityCategory;
import com.tripbudget.tripbudget_core.plan.enums.ActivityStatus;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalTime;

public record UpdateActivityRequest(
        @NotBlank(message = "Title is required")
        String title,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        ActivityCategory category,
        BigDecimal estimatedCost,
        ActivityStatus status,
        Integer orderIndex,
        String note,
        String expenseId,
        String targetDayId
) {}

