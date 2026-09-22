package com.tripbudget.tripbudget_core.plan.dtos.request;

import com.tripbudget.tripbudget_core.plan.enums.ChecklistCategory;

public record UpdateChecklistRequest(
        String title,
        ChecklistCategory category,
        Boolean isCompleted,
        String assigneeId
) {}

