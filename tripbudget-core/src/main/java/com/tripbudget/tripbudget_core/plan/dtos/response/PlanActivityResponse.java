package com.tripbudget.tripbudget_core.plan.dtos.response;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.plan.entities.PlanActivityEntity;
import com.tripbudget.tripbudget_core.plan.enums.ActivityCategory;
import com.tripbudget.tripbudget_core.plan.enums.ActivityStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

public record PlanActivityResponse(
        String id,
        String dayId,
        String tripId,
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
        BigDecimal actualSpent,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlanActivityResponse from(PlanActivityEntity entity, HashidsService hashidsService, BigDecimal actualSpent) {
        return new PlanActivityResponse(
                hashidsService.encode(entity.getId()),
                hashidsService.encode(entity.getDay().getId()),
                hashidsService.encode(entity.getTripId()),
                entity.getTitle(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getLocation(),
                entity.getCategory(),
                entity.getEstimatedCost() != null ? entity.getEstimatedCost() : BigDecimal.ZERO,
                entity.getStatus(),
                entity.getOrderIndex(),
                entity.getNote(),
                entity.getExpenseId() != null ? hashidsService.encode(entity.getExpenseId()) : null,
                actualSpent,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static PlanActivityResponse from(PlanActivityEntity entity, HashidsService hashidsService) {
        return from(entity, hashidsService, null);
    }
}

