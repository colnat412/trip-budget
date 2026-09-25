package com.tripbudget.tripbudget_core.plan.entities;

import com.tripbudget.tripbudget_core.common.entities.BaseEntity;
import com.tripbudget.tripbudget_core.plan.enums.ActivityLogAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "plan_activity_logs"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanActivityLogEntity extends BaseEntity {

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "day_id")
    private Long dayId;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityLogAction action;

    @Column(name = "activity_title", nullable = false, length = 255)
    private String activityTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    public static PlanActivityLogEntity create(
            Long tripId,
            Long dayId,
            Long activityId,
            Long userId,
            ActivityLogAction action,
            String activityTitle,
            String description
    ) {
        PlanActivityLogEntity log = new PlanActivityLogEntity();
        log.tripId = tripId;
        log.dayId = dayId;
        log.activityId = activityId;
        log.userId = userId;
        log.action = action;
        log.activityTitle = activityTitle;
        log.description = description;
        return log;
    }
}
