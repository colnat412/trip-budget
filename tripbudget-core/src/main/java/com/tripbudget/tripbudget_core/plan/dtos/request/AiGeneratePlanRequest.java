package com.tripbudget.tripbudget_core.plan.dtos.request;

import lombok.Data;

@Data
public class AiGeneratePlanRequest {
    private String destination;
    private int days;
    private double budget;
    private int people;
    private String preferences;
}
