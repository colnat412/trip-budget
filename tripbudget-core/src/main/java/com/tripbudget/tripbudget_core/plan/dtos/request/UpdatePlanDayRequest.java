package com.tripbudget.tripbudget_core.plan.dtos.request;

import java.time.LocalDate;

public record UpdatePlanDayRequest(
        LocalDate planDate,
        String title,
        String note
) {}

