package com.tripbudget.tripbudget_core.settlement.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record TripSettlementSummaryResponse(
        String tripId,
        String currency,
        BigDecimal totalExpenses,
        BigDecimal totalSettled,
        BigDecimal myBalance,
        String myStatus,
        List<MemberBalanceResponse> memberBalances,
        List<SuggestedSettlementResponse> suggestedSettlements,
        List<SettlementResponse> settlementHistory
) {}

