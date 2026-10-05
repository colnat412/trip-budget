package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.trip.dtos.response.PublicTripSnapshot;
import com.tripbudget.tripbudget_core.trip.dtos.response.PublicTripViewerResponse;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripResponse;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;
import com.tripbudget.tripbudget_core.trip.services.PublicTripSnapshotService;
import com.tripbudget.tripbudget_core.trip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/public/trips")
@RequiredArgsConstructor
public class PublicTripController {

    private final TripService tripService;
    private final PublicTripSnapshotService publicTripSnapshotService;

    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<PublicTripSnapshot>> getPublicTrip(
            @PathVariable("token") String token
    ) {
        PublicTripSnapshot response = publicTripSnapshotService.getSnapshot(token);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(30))
                        .sMaxAge(Duration.ofSeconds(60))
                        .staleWhileRevalidate(Duration.ofMinutes(5))
                        .cachePublic())
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "Public trip details retrieved successfully",
                        response
                ));
    }

    @GetMapping("/{token}/me")
    public ResponseEntity<ApiResponse<PublicTripViewerResponse>> getPublicTripViewer(
            @PathVariable("token") String token,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        TripMemberStatus status = tripService.getPublicMemberStatus(token, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Viewer status retrieved successfully",
                new PublicTripViewerResponse(status)
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
