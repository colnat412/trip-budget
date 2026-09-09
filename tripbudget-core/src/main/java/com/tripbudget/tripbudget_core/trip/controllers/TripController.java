package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.trip.dtos.request.CreateTripRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.GetAllTripsRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.UpdateTripRequest;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/trip")
@RequiredArgsConstructor
@Validated
public class TripController {
    private final TripService tripService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(@Valid @RequestBody() CreateTripRequest createTripRequest, @CurrentUser CurrentUserDto user) {
        Long currentUserId = user.id();
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

    @GetMapping("/my-trips")
    public ResponseEntity<ApiResponse<PageResponse<TripResponse>>> getMyTrips(
            @CurrentUser CurrentUserDto user,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be greater than or equal to 0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
    ) {
        Long currentUserId = user.id();

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(@PathVariable("id") Long tripId, @CurrentUser CurrentUserDto user) {
        Long currentUserId = user.id();

        TripResponse response = tripService.getTripById(tripId, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Get trip successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> update(
            @PathVariable("id") Long tripId,
            @CurrentUser CurrentUserDto user,
            @Valid @RequestBody()UpdateTripRequest body
    ){
        Long currentUserId = user.id();
        TripResponse response = tripService.update(currentUserId, tripId, body);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Update trip successfully",
                        response
                )
        );
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> deleteTrip(
            @PathVariable("id") Long tripId,
            @CurrentUser CurrentUserDto user,
            @Valid @RequestBody()UpdateTripRequest body
    ){
        Long currentUserId = user.id();

        tripService.deleteTrip(currentUserId, tripId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Delete trip successfully",
                        null
                )
        );
    }
}
