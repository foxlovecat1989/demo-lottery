package org.example.demolottery.dto.request;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class UpdateActivityRequest {
    
    private String name;
    private String description;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "Max draws per user must be at least 1")
    private Integer maxDrawsPerUser;
    
    @Min(value = 1, message = "Max concurrent draws must be at least 1")
    private Integer maxConcurrentDraws;

    public UpdateActivityRequest() {}

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
} 