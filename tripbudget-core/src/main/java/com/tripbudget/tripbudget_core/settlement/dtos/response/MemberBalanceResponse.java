package com.tripbudget.tripbudget_core.settlement.dtos.response;

import java.math.BigDecimal;

public record MemberBalanceResponse(
        String userId,
        String name,
        String email,
        String avatarUrl,
        BigDecimal totalPaid,
        BigDecimal totalShare,
        BigDecimal settledPaid,
        BigDecimal settledReceived,
        BigDecimal netBalance,
        String status
) {}

