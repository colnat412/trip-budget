package com.tripbudget.tripbudget_core.trip.dtos.request;

import com.tripbudget.tripbudget_core.trip.enums.TripMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        TripMemberRole role
) {
    public InviteMemberRequest {
        if (role == null) {
            role = TripMemberRole.MEMBER;
        }
    }
}

