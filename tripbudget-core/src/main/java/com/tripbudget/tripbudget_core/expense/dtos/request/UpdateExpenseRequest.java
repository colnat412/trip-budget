package com.tripbudget.tripbudget_core.expense.dtos.request;

import com.tripbudget.tripbudget_core.expense.enums.ExpenseCategory;
import com.tripbudget.tripbudget_core.expense.enums.SplitType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateExpenseRequest(
        String title,

        ExpenseCategory category,

        @Positive(message = "Amount must be greater than 0")
        BigDecimal amount,

        String currency,

        LocalDate expenseDate,

        SplitType splitType,

        String note,

        String receiptUrl,

        Long payerId,

        List<SplitItemRequest> splits
) {}

