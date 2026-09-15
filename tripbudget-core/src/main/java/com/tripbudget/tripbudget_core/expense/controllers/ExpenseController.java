package com.tripbudget.tripbudget_core.expense.controllers;

import com.tripbudget.tripbudget_core.common.annotations.CurrentUser;
import com.tripbudget.tripbudget_core.common.dtos.ApiResponse;
import com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto;
import com.tripbudget.tripbudget_core.expense.dtos.request.CreateExpenseRequest;
import com.tripbudget.tripbudget_core.expense.dtos.request.ExpenseFilterRequest;
import com.tripbudget.tripbudget_core.expense.dtos.request.SetBudgetRequest;
import com.tripbudget.tripbudget_core.expense.dtos.request.UpdateExpenseRequest;
import com.tripbudget.tripbudget_core.expense.dtos.response.ExpenseResponse;
import com.tripbudget.tripbudget_core.expense.dtos.response.TripBudgetSummaryResponse;
import com.tripbudget.tripbudget_core.expense.services.BudgetService;
import com.tripbudget.tripbudget_core.expense.services.ExpenseService;
import com.tripbudget.tripbudget_core.trip.dtos.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import com.tripbudget.tripbudget_core.common.services.HashidsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip/{tripId}")
@RequiredArgsConstructor
@Validated
public class ExpenseController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final HashidsService hashidsService;

    @PostMapping("/expenses")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @PathVariable("tripId") String tripIdHash,
            @Valid @RequestBody CreateExpenseRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        ExpenseResponse response = expenseService.createExpense(currentUserId, tripId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Expense created successfully",
                        response
                ));
    }

    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse<PageResponse<ExpenseResponse>>> getTripExpenses(
            @PathVariable("tripId") String tripIdHash,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String payer,
            @RequestParam(required = false) String splitType,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);

        ExpenseFilterRequest filter = new ExpenseFilterRequest(
                search,
                title,
                category,
                payer,
                splitType,
                sortBy,
                sortDirection
        );

        PageResponse<ExpenseResponse> response = expenseService.getTripExpenses(
                currentUserId,
                tripId,
                page,
                size,
                filter
        );

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Trip expenses retrieved successfully",
                response
        ));
    }

    @GetMapping("/expenses/summary")
    public ResponseEntity<ApiResponse<TripBudgetSummaryResponse>> getBudgetSummary(
            @PathVariable("tripId") String tripIdHash,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        TripBudgetSummaryResponse response = budgetService.getBudgetSummary(currentUserId, tripId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Budget summary retrieved successfully",
                response
        ));
    }

    @GetMapping("/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseDetail(
            @PathVariable("tripId") String tripIdHash,
            @PathVariable("expenseId") String expenseIdHash,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        Long expenseId = hashidsService.decode(expenseIdHash);
        ExpenseResponse response = expenseService.getExpenseDetail(currentUserId, tripId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Expense detail retrieved successfully",
                response
        ));
    }

    @PutMapping("/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable("tripId") String tripIdHash,
            @PathVariable("expenseId") String expenseIdHash,
            @Valid @RequestBody UpdateExpenseRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        Long expenseId = hashidsService.decode(expenseIdHash);
        ExpenseResponse response = expenseService.updateExpense(currentUserId, tripId, expenseId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Expense updated successfully",
                response
        ));
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable("tripId") String tripIdHash,
            @PathVariable("expenseId") String expenseIdHash,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        Long expenseId = hashidsService.decode(expenseIdHash);
        expenseService.deleteExpense(currentUserId, tripId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Expense deleted successfully",
                null
        ));
    }

    @PostMapping("/budget")
    public ResponseEntity<ApiResponse<TripBudgetSummaryResponse>> setBudget(
            @PathVariable("tripId") String tripIdHash,
            @Valid @RequestBody SetBudgetRequest request,
            @CurrentUser CurrentUserDto user
    ) {
        Long currentUserId = user.id();
        Long tripId = hashidsService.decode(tripIdHash);
        TripBudgetSummaryResponse response = budgetService.setBudget(currentUserId, tripId, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Budget configured successfully",
                response
        ));
    }
}

