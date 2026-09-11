package com.tripbudget.tripbudget_core.expense.dtos.response;

import java.math.BigDecimal;

public record ExpenseSplitResponse(
        String id,
        String userId,
        String userName,
        String userEmail,
        String userAvatarUrl,
        BigDecimal allocatedAmount,
        BigDecimal splitValue,
        boolean settled
) {}

