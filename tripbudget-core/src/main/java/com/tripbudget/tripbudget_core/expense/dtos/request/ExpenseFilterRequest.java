package com.tripbudget.tripbudget_core.expense.dtos.request;

public record ExpenseFilterRequest(
        String search,
        String title,
        String category,
        String payer,
        String splitType,
        String sortBy,
        String sortDirection
) {}

