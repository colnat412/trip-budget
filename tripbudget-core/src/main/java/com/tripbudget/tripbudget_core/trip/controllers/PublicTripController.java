package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.plan.dtos.response.TripPlanOverviewResponse;
import com.tripbudget.tripbudget_core.plan.services.PlanService;
import com.tripbudget.tripbudget_core.trip.dtos.response.PublicTripResponse;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/trips")
@RequiredArgsConstructor
public class PublicTripController {

    private final TripService tripService;
    private final PlanService planService;

    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<PublicTripResponse>> getPublicTrip(
            @PathVariable("token") String token,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user != null ? user.id() : null;
        PublicTripResponse response = tripService.getPublicTrip(token, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Public trip details retrieved successfully",
                response
        ));
    }

    @GetMapping("/{token}/plan")
    public ResponseEntity<ApiResponse<TripPlanOverviewResponse>> getPublicTripPlan(
            @PathVariable("token") String token
    ) {
        TripPlanOverviewResponse response = planService.getPublicTripPlanOverview(token);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Public trip plan retrieved successfully",
                response
        ));
    }

    @PostMapping("/{token}/join")
    public ResponseEntity<ApiResponse<TripResponse>> joinPublicTrip(
            @PathVariable("token") String token,
            @CurrentUser CurrentUserDto user
    ) {
        if (user == null || user.id() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in to join this trip"
            );
        }

        TripResponse response = tripService.joinPublicTrip(token, user.id());
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Joined trip successfully",
                response
        ));
    }
}
