package com.tripbudget.tripbudget_core.expense.dtos.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record SplitItemRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        BigDecimal splitValue,

        BigDecimal allocatedAmount
) {}

