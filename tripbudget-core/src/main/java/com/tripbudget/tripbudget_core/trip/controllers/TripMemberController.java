package com.tripbudget.tripbudget_core.trip.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.trip.dtos.request.InviteMemberRequest;
import com.tripbudget.tripbudget_core.trip.dtos.request.UpdateMemberRoleRequest;
import com.tripbudget.tripbudget_core.trip.dtos.response.TripMemberResponse;
import com.tripbudget.tripbudget_core.trip.services.TripMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trip/{tripId}/members")
@RequiredArgsConstructor
@Validated
public class TripMemberController {

    private final TripMemberService tripMemberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripMemberResponse>>> getTripMembers(
            @PathVariable("tripId") Long tripId,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        List<TripMemberResponse> response = tripMemberService.getTripMembers(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Trip members retrieved successfully",
                response
        ));
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<TripMemberResponse>> inviteMember(
            @PathVariable("tripId") Long tripId,
            @Valid @RequestBody InviteMemberRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        TripMemberResponse response = tripMemberService.inviteMember(currentUserId, tripId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Member invited successfully",
                        response
                ));
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<ApiResponse<TripMemberResponse>> updateMemberRole(
            @PathVariable("tripId") Long tripId,
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody UpdateMemberRoleRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        TripMemberResponse response = tripMemberService.updateMemberRole(currentUserId, tripId, memberId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Member role updated successfully",
                response
        ));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable("tripId") Long tripId,
            @PathVariable("memberId") Long memberId,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        tripMemberService.removeMember(currentUserId, tripId, memberId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Member removed successfully",
                null
        ));
    }

    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leaveTrip(
            @PathVariable("tripId") Long tripId,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        tripMemberService.leaveTrip(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "You have left the trip successfully",
                null
        ));
    }
}

