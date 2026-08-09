package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllTripsRequest {
    @NotBlank(message = "Member status is required")
    private TripMemberStatus memberStatus;
}