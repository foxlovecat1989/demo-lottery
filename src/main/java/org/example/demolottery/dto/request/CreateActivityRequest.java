package org.example.demolottery.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class CreateActivityRequest {
    
    @NotBlank(message = "Activity name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    @NotNull(message = "Max draws per user is required")
    @Min(value = 1, message = "Max draws per user must be at least 1")
    private Integer maxDrawsPerUser;
    
    @NotNull(message = "Max concurrent draws is required")
    @Min(value = 1, message = "Max concurrent draws must be at least 1")
    private Integer maxConcurrentDraws;
    
    @Valid
    private List<CreatePrizeRequest> prizes;

    public CreateActivityRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxDrawsPerUser() {
        return maxDrawsPerUser;
    }

    public void setMaxDrawsPerUser(Integer maxDrawsPerUser) {
        this.maxDrawsPerUser = maxDrawsPerUser;
    }

    public Integer getMaxConcurrentDraws() {
        return maxConcurrentDraws;
    }

    public void setMaxConcurrentDraws(Integer maxConcurrentDraws) {
        this.maxConcurrentDraws = maxConcurrentDraws;
    }

    public List<CreatePrizeRequest> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<CreatePrizeRequest> prizes) {
        this.prizes = prizes;
    }
} 