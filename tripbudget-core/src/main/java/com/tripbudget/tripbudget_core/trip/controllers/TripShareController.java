package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.trip.dtos.request.UpdateShareSettingsRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripShareResponse;
import com.tripbudget.tripbudget_core.trip.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip/{tripId}/share")
@RequiredArgsConstructor
@Validated
public class TripShareController {

    private final TripService tripService;
    private final HashidsService hashidsService;

    @GetMapping
    public ResponseEntity<ApiResponse<TripShareResponse>> getShareSettings(
            @PathVariable("tripId") String tripIdHash,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        TripShareResponse response = tripService.getShareSettings(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Share settings retrieved successfully",
                response
        ));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TripShareResponse>> updateShareSettings(
            @PathVariable("tripId") String tripIdHash,
            @Valid @RequestBody UpdateShareSettingsRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        TripShareResponse response = tripService.updateShareSettings(currentUserId, tripId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Share settings updated successfully",
                response
        ));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ApiResponse<TripShareResponse>> regenerateShareToken(
            @PathVariable("tripId") String tripIdHash,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        TripShareResponse response = tripService.regenerateShareToken(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Share link regenerated successfully",
                response
        ));
    }
}
