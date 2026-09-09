package com.tripbudget.tripbudget_core.expense.dtos.response;

import com.tripbudget.tripbudget_core.expense.enums.ExpenseCategory;

import java.math.BigDecimal;

public record CategoryBreakdownResponse(
        ExpenseCategory category,
        BigDecimal spentAmount,
        BigDecimal limitAmount,
        double percentageUsed
) {}

