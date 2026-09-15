package com.tripbudget.tripbudget_core.settlement.dtos.response;

import java.math.BigDecimal;

public record SuggestedSettlementResponse(
        String fromUserId,
        String fromUserName,
        String fromUserEmail,
        String fromUserAvatarUrl,
        String toUserId,
        String toUserName,
        String toUserEmail,
        String toUserAvatarUrl,
        BigDecimal amount,
        String currency
) {}

