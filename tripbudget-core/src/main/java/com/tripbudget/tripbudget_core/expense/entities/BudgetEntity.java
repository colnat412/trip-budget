package com.tripbudget.tripbudget_core.expense.entities;

import com.tripbudget.tripbudget_core.common.entities.BaseEntity;
import com.tripbudget.tripbudget_core.expense.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_budgets_trip",
                        columnNames = {"trip_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetEntity extends BaseEntity {

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "total_budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalBudget;

    @Column(nullable = false, length = 3)
    private String currency;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryBudgetEntity> categoryBudgets = new ArrayList<>();

    public static BudgetEntity create(Long tripId, BigDecimal totalBudget, String currency) {
        BudgetEntity budget = new BudgetEntity();
        budget.tripId = tripId;
        budget.totalBudget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
        budget.currency = currency != null ? currency.trim().toUpperCase(Locale.ROOT) : "VND";
        return budget;
    }

    public void update(BigDecimal totalBudget, String currency) {
        if (totalBudget != null) {
            this.totalBudget = totalBudget;
        }
        if (currency != null && !currency.isBlank()) {
            this.currency = currency.trim().toUpperCase(Locale.ROOT);
        }
    }

    public void setCategoryLimit(ExpenseCategory category, BigDecimal limitAmount) {
        if (category == null || limitAmount == null) {
            return;
        }

        for (CategoryBudgetEntity cb : this.categoryBudgets) {
            if (cb.getCategory() == category) {
                cb.updateLimit(limitAmount);
                return;
            }
        }

        CategoryBudgetEntity newLimit = CategoryBudgetEntity.create(this, category, limitAmount);
        this.categoryBudgets.add(newLimit);
    }
}

