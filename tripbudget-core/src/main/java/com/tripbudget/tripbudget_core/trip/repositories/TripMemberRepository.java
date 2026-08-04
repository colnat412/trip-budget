package com.tripbudget.tripbudget_core.trip.repositories;

import com.tripbudget.tripbudget_core.trip.entities.TripMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMemberEntity, Long> {
}
