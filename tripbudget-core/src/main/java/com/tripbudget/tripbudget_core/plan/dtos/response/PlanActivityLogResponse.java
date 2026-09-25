package com.tripbudget.tripbudget_core.plan.dtos.response;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.plan.entities.PlanActivityLogEntity;
import com.tripbudget.tripbudget_core.plan.enums.ActivityLogAction;
import com.tripbudget.tripbudget_core.user.entities.UserEntity;

import java.time.Instant;

public record PlanActivityLogResponse(
        String id,
        String activityId,
        String activityTitle,
        ActivityLogAction action,
        String description,
        String userId,
        String userName,
        String userAvatar,
        String userEmail,
        Instant createdAt
) {
    public static PlanActivityLogResponse from(
            PlanActivityLogEntity entity,
            UserEntity user,
            HashidsService hashidsService
    ) {
        return new PlanActivityLogResponse(
                hashidsService.encode(entity.getId()),
                entity.getActivityId() != null ? hashidsService.encode(entity.getActivityId()) : null,
                entity.getActivityTitle(),
                entity.getAction(),
                entity.getDescription(),
                hashidsService.encode(entity.getUserId()),
                user != null ? user.getName() : "Người dùng",
                user != null ? user.getAvatarUrl() : null,
                user != null ? user.getEmail() : null,
                entity.getCreatedAt()
        );
    }
}
