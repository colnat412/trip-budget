package com.tripbudget.tripbudget_core.plan.dtos.response;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.plan.entities.PlanChecklistEntity;
import com.tripbudget.tripbudget_core.plan.enums.ChecklistCategory;
import com.tripbudget.tripbudget_core.user.entities.UserEntity;

import java.time.Instant;

public record PlanChecklistResponse(
        String id,
        String tripId,
        String title,
        ChecklistCategory category,
        boolean isCompleted,
        String assigneeId,
        String assigneeName,
        String assigneeEmail,
        String assigneeAvatarUrl,
        Instant createdAt
) {
    public static PlanChecklistResponse from(PlanChecklistEntity entity, UserEntity assignee, HashidsService hashidsService) {
        return new PlanChecklistResponse(
                hashidsService.encode(entity.getId()),
                hashidsService.encode(entity.getTripId()),
                entity.getTitle(),
                entity.getCategory(),
                entity.isCompleted(),
                entity.getAssigneeId() != null ? hashidsService.encode(entity.getAssigneeId()) : null,
                assignee != null ? assignee.getName() : null,
                assignee != null ? assignee.getEmail() : null,
                assignee != null ? assignee.getAvatarUrl() : null,
                entity.getCreatedAt()
        );
    }
}

