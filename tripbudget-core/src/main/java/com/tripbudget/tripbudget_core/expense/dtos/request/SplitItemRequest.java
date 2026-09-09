package com.tripbudget.tripbudget_core.expense.dtos.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SplitItemRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        BigDecimal splitValue,

        BigDecimal allocatedAmount
) {}

