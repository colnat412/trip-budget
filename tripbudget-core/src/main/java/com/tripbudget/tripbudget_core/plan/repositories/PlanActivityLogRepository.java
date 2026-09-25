package com.tripbudget.tripbudget_core.plan.repositories;

import com.tripbudget.tripbudget_core.plan.entities.PlanActivityLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanActivityLogRepository extends JpaRepository<PlanActivityLogEntity, Long> {
    List<PlanActivityLogEntity> findAllByTripIdAndIsDelFalseOrderByCreatedAtDesc(Long tripId, Pageable pageable);
    List<PlanActivityLogEntity> findAllByTripIdAndIsDelFalseOrderByCreatedAtDesc(Long tripId);
}
