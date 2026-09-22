package com.tripbudget.tripbudget_core.plan.repositories;

import com.tripbudget.tripbudget_core.plan.entities.PlanChecklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanChecklistRepository extends JpaRepository<PlanChecklistEntity, Long> {

    List<PlanChecklistEntity> findAllByTripIdAndIsDelFalseOrderByCreatedAtAsc(Long tripId);

    Optional<PlanChecklistEntity> findByIdAndTripIdAndIsDelFalse(Long id, Long tripId);
}

