package com.tripbudget.tripbudget_core.user.repositories;

import com.tripbudget.tripbudget_core.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByIdIn(Collection<Long> ids);
}

