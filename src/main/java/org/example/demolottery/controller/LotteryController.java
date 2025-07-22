package org.example.demolottery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demolottery.dto.request.LotteryDrawRequest;
import org.example.demolottery.dto.response.LotteryDrawResponse;
import org.example.demolottery.service.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lottery")
@Tag(name = "Lottery", description = "Lottery drawing operations")
@SecurityRequirement(name = "Bearer Authentication")
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/draw")
    @Operation(
        summary = "Draw lottery",
        description = "Perform lottery drawing for authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully performed draw"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User has exceeded draw limit")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LotteryDrawResponse> draw(
            @Valid @RequestBody LotteryDrawRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        LotteryDrawResponse response = lotteryService.performDraw(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/draw-count/{activityId}")
    @Operation(
        summary = "Get user draw count",
        description = "Get number of draws used by user for specific activity"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved draw count"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getUserDrawCount(
            @PathVariable Long activityId,
            Authentication authentication) {
        String userId = authentication.getName();
        long count = lotteryService.getUserDrawCount(userId, activityId);
        return ResponseEntity.ok(count);
    }
} 