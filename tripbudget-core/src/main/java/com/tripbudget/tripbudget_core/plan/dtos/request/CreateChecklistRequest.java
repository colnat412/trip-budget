package com.tripbudget.tripbudget_core.plan.dtos.request;

import com.tripbudget.tripbudget_core.plan.enums.ChecklistCategory;
import jakarta.validation.constraints.NotBlank;

public record CreateChecklistRequest(
        @NotBlank(message = "Title is required")
        String title,

        ChecklistCategory category,

        String assigneeId
) {}

