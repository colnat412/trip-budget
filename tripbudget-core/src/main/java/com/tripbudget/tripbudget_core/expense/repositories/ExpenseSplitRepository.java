package com.tripbudget.tripbudget_core.expense.repositories;

import com.tripbudget.tripbudget_core.expense.entities.ExpenseSplitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplitEntity, Long> {

    List<ExpenseSplitEntity> findByExpenseIdAndIsDelFalse(Long expenseId);

    List<ExpenseSplitEntity> findByUserIdAndIsDelFalse(Long userId);
}

