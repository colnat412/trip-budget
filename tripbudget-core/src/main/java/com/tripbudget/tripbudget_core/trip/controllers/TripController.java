package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.trip.dtos.request.CreateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.GetAllTripsRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.PageResponse;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.services.TripService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController()
@RequestMapping("/trips")
@RequiredArgsConstructor
@Validated
public class TripController {
    private final TripService tripService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(@Valid @RequestBody() CreateTripRequest createTripRequest, @AuthenticationPrincipal Jwt jwt) {
        Long currentUserId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        TripResponse response = tripService.createTrip(currentUserId, createTripRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                HttpStatus.CREATED,
                                "Trip created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TripResponse>>> getMyTrips(
            @AuthenticationPrincipal Jwt jwt,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be greater than or equal to 0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
    ) {
        Long currentUserId = Long.parseLong(Objects.requireNonNull(jwt.getSubject()));

        PageResponse<TripResponse> response = tripService.getMyTrips(
                currentUserId,
                page,
                size
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Trips retrieved successfully",
                        response
                )
        );
    }
}
