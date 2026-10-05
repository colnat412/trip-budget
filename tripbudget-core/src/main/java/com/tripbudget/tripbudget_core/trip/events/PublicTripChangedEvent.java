package com.tripbudget.tripbudget_core.trip.events;

public record PublicTripChangedEvent(Long tripId, String revokedShareToken) {

    public static PublicTripChangedEvent of(Long tripId) {
        return new PublicTripChangedEvent(tripId, null);
    }
}
