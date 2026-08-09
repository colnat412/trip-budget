package com.tripbudget.tripbudget_core.trip.entities;

import com.tripbudget.tripbudget_core.trip.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;

@Getter
@Entity
@Table(
        schema = "trip_core",
        name = "trips"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // set to service only use .create(...) cannot use new TripEntity();
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 255)
    private String destination;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TripStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // default set value to create trip, prevent set incorrectly value
    public static TripEntity create(
            Long ownerId,
            String name,
            String destination,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String baseCurrency
    ) {
        TripEntity trip = new TripEntity();

        trip.ownerId = ownerId;
        trip.name = name.trim();
        trip.destination = trimToNull(destination);
        trip.description = trimToNull(description);
        trip.startDate = startDate;
        trip.endDate = endDate;

        trip.baseCurrency = baseCurrency == null
                ? "VND"
                : baseCurrency.toUpperCase(Locale.ROOT);

        // always DRAFT when create trip
        trip.status = TripStatus.DRAFT;

        return trip;
    }

    public void update(
            String name,
            String description,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String baseCurrency
    ) {
        if (name != null) {
            this.name = name.trim();
        }

        if (description != null) {
            this.description = description;
        }

        if (destination != null) {
            this.destination = destination;
        }

        if (startDate != null) {
            this.startDate = startDate;
        }

        if (endDate != null) {
            this.endDate = endDate;
        }

        if (baseCurrency != null) {
            this.baseCurrency = baseCurrency
                    .trim()
                    .toUpperCase(Locale.ROOT);
        }
    }

    public void deleteTrip() {
        this.status = TripStatus.DELETED;
    }

    @PrePersist
    private void beforeInsert() {
        Instant now = Instant.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}