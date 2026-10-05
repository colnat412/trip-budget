package com.tripbudget.tripbudget_core.expense.entities;

import com.tripbudget.tripbudget_core.common.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "expense_splits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expense_user_split",
                        columnNames = {"expense_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_splits_user", columnList = "user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseSplitEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private ExpenseEntity expense;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "allocated_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(name = "split_value", precision = 18, scale = 4)
    private BigDecimal splitValue;

    @Column(nullable = false)
    private boolean settled = false;

    public static ExpenseSplitEntity create(
            ExpenseEntity expense,
            Long userId,
            BigDecimal allocatedAmount,
            BigDecimal splitValue
    ) {
        ExpenseSplitEntity split = new ExpenseSplitEntity();
        split.expense = expense;
        split.userId = userId;
        split.allocatedAmount = allocatedAmount != null ? allocatedAmount : BigDecimal.ZERO;
        split.splitValue = splitValue;
        split.settled = false;
        return split;
    }

    public void update(BigDecimal allocatedAmount, BigDecimal splitValue) {
        if (allocatedAmount != null) {
            this.allocatedAmount = allocatedAmount;
        }
        this.splitValue = splitValue;
    }

    public void markSettled(boolean settled) {
        this.settled = settled;
    }
}

