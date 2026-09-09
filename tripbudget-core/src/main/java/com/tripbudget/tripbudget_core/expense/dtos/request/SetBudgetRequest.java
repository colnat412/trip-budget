package com.tripbudget.tripbudget_core.expense.dtos.request;

import com.tripbudget.tripbudget_core.expense.enums.ExpenseCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record SetBudgetRequest(
        @NotNull(message = "Total budget is required")
        @Min(value = 0, message = "Total budget cannot be negative")
        BigDecimal totalBudget,

        String currency,

        Map<ExpenseCategory, BigDecimal> categoryLimits
) {}

