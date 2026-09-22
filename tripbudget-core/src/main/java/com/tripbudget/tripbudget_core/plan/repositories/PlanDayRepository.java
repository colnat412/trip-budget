package com.tripbudget.tripbudget_core.plan.repositories;

import com.tripbudget.tripbudget_core.plan.entities.PlanDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanDayRepository extends JpaRepository<PlanDayEntity, Long> {

    List<PlanDayEntity> findAllByTripIdAndIsDelFalseOrderByDayNumberAsc(Long tripId);

    Optional<PlanDayEntity> findByIdAndTripIdAndIsDelFalse(Long id, Long tripId);

    Optional<PlanDayEntity> findByTripIdAndDayNumberAndIsDelFalse(Long tripId, Integer dayNumber);

    boolean existsByTripIdAndIsDelFalse(Long tripId);
}

