package com.tripbudget.tripbudget_core.expense.entities;

import com.tripbudget.tripbudget_core.common.entities.BaseEntity;
import com.tripbudget.tripbudget_core.expense.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "category_budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_category_budgets",
                        columnNames = {"budget_id", "category"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBudgetEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private BudgetEntity budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExpenseCategory category;

    @Column(name = "limit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitAmount;

    public static CategoryBudgetEntity create(BudgetEntity budget, ExpenseCategory category, BigDecimal limitAmount) {
        CategoryBudgetEntity entity = new CategoryBudgetEntity();
        entity.budget = budget;
        entity.category = category;
        entity.limitAmount = limitAmount != null ? limitAmount : BigDecimal.ZERO;
        return entity;
    }

    public void updateLimit(BigDecimal limitAmount) {
        if (limitAmount != null) {
            this.limitAmount = limitAmount;
        }
    }
}

