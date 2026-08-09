package com.tripbudget.tripbudget_core.trip.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTripRequest {

    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String destination;

    @Size(max = 5000)
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "Base currency must be a 3-letter uppercase code"
    )
    private String baseCurrency = "VND";
}
