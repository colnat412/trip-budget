package com.tripbudget.tripbudget_core.trip.controllers;

import com.nimbusds.jwt.JWT;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.trip.dtos.request.CreateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.services.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController()
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

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
}
