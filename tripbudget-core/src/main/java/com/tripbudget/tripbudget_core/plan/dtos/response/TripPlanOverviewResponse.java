package com.tripbudget.tripbudget_core.plan.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TripPlanOverviewResponse(
        String tripId,
        String tripName,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        String baseCurrency,
        int totalDays,
        BigDecimal totalEstimatedCost,
        int totalActivities,
        int completedActivities,
        List<PlanDayResponse> days,
        List<PlanChecklistResponse> checklists
) {}

