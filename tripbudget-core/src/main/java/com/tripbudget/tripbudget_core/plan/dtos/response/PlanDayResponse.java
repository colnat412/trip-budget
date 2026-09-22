package com.tripbudget.tripbudget_core.plan.dtos.response;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.plan.entities.PlanDayEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PlanDayResponse(
        String id,
        String tripId,
        Integer dayNumber,
        LocalDate planDate,
        String title,
        String note,
        BigDecimal totalEstimatedCost,
        int totalActivities,
        List<PlanActivityResponse> activities
) {
    public static PlanDayResponse from(PlanDayEntity entity, List<PlanActivityResponse> activities, HashidsService hashidsService) {
        BigDecimal total = activities.stream()
                .map(PlanActivityResponse::estimatedCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PlanDayResponse(
                hashidsService.encode(entity.getId()),
                hashidsService.encode(entity.getTripId()),
                entity.getDayNumber(),
                entity.getPlanDate(),
                entity.getTitle(),
                entity.getNote(),
                total,
                activities.size(),
                activities
        );
    }
}

