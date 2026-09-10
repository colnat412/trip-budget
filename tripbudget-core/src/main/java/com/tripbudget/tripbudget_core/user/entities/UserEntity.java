package com.tripbudget.tripbudget_core.user.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(schema = "public", name = "users")
@Immutable
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    private String status;
}

