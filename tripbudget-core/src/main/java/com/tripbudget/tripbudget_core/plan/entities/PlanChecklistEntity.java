package com.tripbudget.tripbudget_core.plan.entities;

import com.tripbudget.tripbudget_core.common.entities.BaseEntity;
import com.tripbudget.tripbudget_core.plan.enums.ChecklistCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "plan_checklists",
        indexes = {
                @Index(name = "idx_plan_checklists_trip", columnList = "trip_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanChecklistEntity extends BaseEntity {

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChecklistCategory category;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "assignee_id")
    private Long assigneeId;

    public static PlanChecklistEntity create(
            Long tripId,
            String title,
            ChecklistCategory category,
            Long assigneeId
    ) {
        PlanChecklistEntity item = new PlanChecklistEntity();
        item.tripId = tripId;
        item.title = title.trim();
        item.category = category != null ? category : ChecklistCategory.OTHER;
        item.isCompleted = false;
        item.assigneeId = assigneeId;
        return item;
    }

    public void updateDetails(String title, ChecklistCategory category, Boolean isCompleted, Long assigneeId) {
        if (title != null && !title.isBlank()) {
            this.title = title.trim();
        }
        if (category != null) {
            this.category = category;
        }
        if (isCompleted != null) {
            this.isCompleted = isCompleted;
        }
        this.assigneeId = assigneeId;
    }

    public void toggle() {
        this.isCompleted = !this.isCompleted;
    }
}

