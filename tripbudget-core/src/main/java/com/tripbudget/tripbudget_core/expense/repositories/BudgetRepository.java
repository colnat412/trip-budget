package com.tripbudget.tripbudget_core.expense.repositories;

import com.tripbudget.tripbudget_core.expense.entities.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {

    Optional<BudgetEntity> findByTripIdAndIsDelFalse(Long tripId);
}

