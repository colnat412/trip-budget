package com.tripbudget.tripbudget_core.trip.specifications;

import com.tripbudget.tripbudget_core.trip.dtos.request.TripFilterRequest;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.entities.TripMemberEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;
import com.tripbudget.tripbudget_core.trip.enums.TripStatus;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TripSpecifications {

    private TripSpecifications() {}

    public static Specification<TripEntity> buildSpecification(Long currentUserId, TripFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Subquery<Long> memberSubquery = query.subquery(Long.class);
            Root<TripMemberEntity> memberRoot = memberSubquery.from(TripMemberEntity.class);
            memberSubquery.select(memberRoot.get("trip").get("id"));
            memberSubquery.where(
                    cb.equal(memberRoot.get("userId"), currentUserId),
                    cb.equal(memberRoot.get("status"), TripMemberStatus.ACTIVE),
                    cb.isFalse(memberRoot.get("isDel"))
            );
            predicates.add(root.get("id").in(memberSubquery));

            predicates.add(cb.notEqual(root.get("status"), TripStatus.DELETED));
            predicates.add(cb.isFalse(root.get("isDel")));

            if (filter != null) {
                if (filter.search() != null && !filter.search().isBlank()) {
                    String pattern = "%" + filter.search().trim().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("name")), pattern),
                            cb.like(cb.lower(root.get("destination")), pattern)
                    ));
                }

                if (filter.name() != null && !filter.name().isBlank()) {
                    String namePattern = "%" + filter.name().trim().toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
                }

                if (filter.destination() != null && !filter.destination().isBlank()) {
                    String destPattern = "%" + filter.destination().trim().toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(root.get("destination")), destPattern));
                }

                if (filter.currency() != null && !filter.currency().isBlank()) {
                    List<String> currencies = Arrays.stream(filter.currency().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(s -> s.toUpperCase(Locale.ROOT))
                            .toList();
                    if (!currencies.isEmpty()) {
                        predicates.add(root.get("baseCurrency").in(currencies));
                    }
                }

                if (filter.status() != null && !filter.status().isBlank()) {
                    List<TripStatus> statuses = Arrays.stream(filter.status().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(s -> {
                                try {
                                    return TripStatus.valueOf(s.toUpperCase(Locale.ROOT));
                                } catch (IllegalArgumentException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    if (!statuses.isEmpty()) {
                        predicates.add(root.get("status").in(statuses));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Sort buildSort(String sortBy, String sortDirection) {
        String property = switch (sortBy == null ? "" : sortBy.trim().toLowerCase()) {
            case "name" -> "name";
            case "destination" -> "destination";
            case "dates", "startdate" -> "startDate";
            case "enddate" -> "endDate";
            case "currency", "basecurrency" -> "baseCurrency";
            case "status" -> "status";
            case "createdat" -> "createdAt";
            default -> "id";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, property);
    }
}

