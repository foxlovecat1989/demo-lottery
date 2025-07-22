package org.example.demolottery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demolottery.dto.request.CreateActivityRequest;
import org.example.demolottery.dto.request.UpdateActivityRequest;
import org.example.demolottery.dto.response.ActivityResponse;
import org.example.demolottery.entity.LotteryActivity;
import org.example.demolottery.service.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities", description = "Lottery activity management")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @Operation(
        summary = "Create activity",
        description = "Create a new lottery activity (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        ActivityResponse response = activityService.createActivity(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{activityId}")
    @Operation(
        summary = "Update activity",
        description = "Update an existing lottery activity (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long activityId,
            @Valid @RequestBody UpdateActivityRequest request) {
        ActivityResponse response = activityService.updateActivity(activityId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{activityId}")
    @Operation(
        summary = "Get activity",
        description = "Get activity details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity found"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    public ResponseEntity<ActivityResponse> getActivity(
            @Parameter(description = "ID of the activity to retrieve")
            @PathVariable Long activityId) {
        ActivityResponse response = activityService.getActivity(activityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get active activities",
        description = "Get paginated list of active lottery activities"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active activities")
    })
    public ResponseEntity<Page<ActivityResponse>> getActiveActivities(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityResponse> response = activityService.getActiveActivities(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all activities",
        description = "Get paginated list of all lottery activities (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activities"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ActivityResponse>> getAllActivities(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityResponse> response = activityService.getAllActivities(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{activityId}/status")
    @Operation(
        summary = "Update activity status",
        description = "Update the status of a lottery activity (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActivityResponse> updateActivityStatus(
            @Parameter(description = "ID of the activity to update")
            @PathVariable Long activityId,
            @Parameter(description = "New activity status (DRAFT, ACTIVE, PAUSED, ENDED)")
            @RequestParam LotteryActivity.ActivityStatus status) {
        ActivityResponse response = activityService.updateActivityStatus(activityId, status);
        return ResponseEntity.ok(response);
    }
} 