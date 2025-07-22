package org.example.demolottery.dto.response;

import org.example.demolottery.entity.LotteryActivity;

import java.time.LocalDateTime;
import java.util.List;

public class ActivityResponse {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxDrawsPerUser;
    private Integer maxConcurrentDraws;
    private LotteryActivity.ActivityStatus status;
    private List<PrizeResponse> prizes;
    private LocalDateTime createdAt;

    public ActivityResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LotteryActivity.ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(LotteryActivity.ActivityStatus status) {
        this.status = status;
    }

    public List<PrizeResponse> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<PrizeResponse> prizes) {
        this.prizes = prizes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 