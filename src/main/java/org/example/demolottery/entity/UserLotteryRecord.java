package org.example.demolottery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_lottery_records", 
       indexes = {
           @Index(name = "idx_user_activity", columnList = "userId,activityId"),
           @Index(name = "idx_activity_time", columnList = "activityId,createdAt")
       })
public class UserLotteryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String userId;

    @NotNull
    @Column(nullable = false)
    private Long activityId;

    @Column(length = 100)
    private String batchId;

    @Column
    private Long prizeId;

    @Column(length = 100)
    private String prizeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrawResult result;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum DrawResult {
        WON, NO_PRIZE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserLotteryRecord() {}

    public UserLotteryRecord(String userId, Long activityId, String batchId, DrawResult result) {
        this.userId = userId;
        this.activityId = activityId;
        this.batchId = batchId;
        this.result = result;
    }

    public UserLotteryRecord(String userId, Long activityId, String batchId, 
                           Long prizeId, String prizeName, DrawResult result) {
        this.userId = userId;
        this.activityId = activityId;
        this.batchId = batchId;
        this.prizeId = prizeId;
        this.prizeName = prizeName;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public DrawResult getResult() {
        return result;
    }

    public void setResult(DrawResult result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 