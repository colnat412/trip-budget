package com.tripbudget.tripbudget_core.expense.enums;

public enum SplitType {
    EQUAL,        // 400 for 4 people => 100 for each person
    EXACT_AMOUNT,  // 100 for A, 150 for B, 50 for C
    PERCENTAGE,    // 50% for A, 30% for B, 20% for C
    SHARE          // 1 share for A, 2 shares for B, 1 share for C
}

