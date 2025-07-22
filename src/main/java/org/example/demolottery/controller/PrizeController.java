package org.example.demolottery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demolottery.dto.request.CreatePrizeRequest;
import org.example.demolottery.dto.response.PrizeResponse;
import org.example.demolottery.service.PrizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities/{activityId}/prizes")
@Tag(name = "Prizes", description = "Prize management for lottery activities")
@SecurityRequirement(name = "Bearer Authentication")
public class PrizeController {

    private final PrizeService prizeService;

    public PrizeController(PrizeService prizeService) {
        this.prizeService = prizeService;
    }

    @PostMapping
    @Operation(
        summary = "Add prize to activity",
        description = "Add a new prize to an existing lottery activity (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prize added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrizeResponse> addPrize(
            @Parameter(description = "ID of the activity to add prize to")
            @PathVariable Long activityId,
            @Valid @RequestBody CreatePrizeRequest request) {
        PrizeResponse response = prizeService.addPrizeToActivity(activityId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get activity prizes",
        description = "Get all prizes for a specific activity"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prizes retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    public ResponseEntity<List<PrizeResponse>> getActivityPrizes(
            @Parameter(description = "ID of the activity")
            @PathVariable Long activityId) {
        List<PrizeResponse> response = prizeService.getActivityPrizes(activityId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prizeId}")
    @Operation(
        summary = "Update prize",
        description = "Update an existing prize (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prize updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Prize not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrizeResponse> updatePrize(
            @Parameter(description = "ID of the activity")
            @PathVariable Long activityId,
            @Parameter(description = "ID of the prize to update")
            @PathVariable Long prizeId,
            @Valid @RequestBody CreatePrizeRequest request) {
        PrizeResponse response = prizeService.updatePrize(activityId, prizeId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{prizeId}")
    @Operation(
        summary = "Delete prize",
        description = "Delete a prize from an activity (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prize deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "Prize not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePrize(
            @Parameter(description = "ID of the activity")
            @PathVariable Long activityId,
            @Parameter(description = "ID of the prize to delete")
            @PathVariable Long prizeId) {
        prizeService.deletePrize(activityId, prizeId);
        return ResponseEntity.ok().build();
    }
} 