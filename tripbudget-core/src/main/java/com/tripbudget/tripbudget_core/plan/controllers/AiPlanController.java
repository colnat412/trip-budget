package com.tripbudget.tripbudget_core.plan.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.plan.dtos.request.AiGeneratePlanRequest;
import com.tripbudget.tripbudget_core.plan.dtos.response.TripPlanOverviewResponse;
import com.tripbudget.tripbudget_core.plan.services.AiPlanService;
import com.tripbudget.tripbudget_core.plan.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip/{tripId}/plan/ai")
@RequiredArgsConstructor
@Validated
public class AiPlanController {

    private final AiPlanService aiPlanService;
    private final PlanService planService;
    private final HashidsService hashidsService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<TripPlanOverviewResponse>> generateAiPlan(
            @PathVariable("tripId") String tripIdHash,
            @RequestBody AiGeneratePlanRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        aiPlanService.generateAndSavePlan(currentUserId, tripId, request);
        TripPlanOverviewResponse response = planService.getTripPlanOverview(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "AI-generated plan created successfully.",
                response
        ));
    }
}
