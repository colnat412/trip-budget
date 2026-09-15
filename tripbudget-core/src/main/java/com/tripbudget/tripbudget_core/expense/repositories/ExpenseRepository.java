package com.tripbudget.tripbudget_core.expense.repositories;

import com.tripbudget.tripbudget_core.expense.entities.ExpenseEntity;
import com.tripbudget.tripbudget_core.expense.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {

    Page<ExpenseEntity> findByTripIdAndStatusNotAndIsDelFalseOrderByExpenseDateDesc(
            Long tripId,
            ExpenseStatus status,
            Pageable pageable
    );

    List<ExpenseEntity> findByTripIdAndStatusNotAndIsDelFalseOrderByExpenseDateDesc(
            Long tripId,
            ExpenseStatus status
    );

    Optional<ExpenseEntity> findByIdAndTripIdAndIsDelFalse(Long id, Long tripId);

    Optional<ExpenseEntity> findByIdAndIsDelFalse(Long id);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e " +
           "WHERE e.tripId = :tripId AND e.status = :status AND e.isDel = false")
    BigDecimal sumAmountByTripIdAndStatus(
            @Param("tripId") Long tripId,
            @Param("status") ExpenseStatus status
    );

    @Query("SELECT e.category, COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e " +
           "WHERE e.tripId = :tripId AND e.status = :status AND e.isDel = false " +
           "GROUP BY e.category")
    List<Object[]> sumAmountByCategory(
            @Param("tripId") Long tripId,
            @Param("status") ExpenseStatus status
    );
}

