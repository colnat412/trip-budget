package com.tripbudget.tripbudget_core.user.repositories;

import com.tripbudget.tripbudget_core.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByIdIn(Collection<Long> ids);

    @Query("SELECT u.id FROM UserEntity u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Long> findIdsByKeyword(@Param("keyword") String keyword);
}


