package org.example.demolottery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "lottery_activities")
public class LotteryActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotNull
    @Min(value = 1, message = "Max draws per user must be at least 1")
    @Column(nullable = false)
    private Integer maxDrawsPerUser;

    @NotNull
    @Min(value = 1, message = "Max concurrent draws must be at least 1")
    @Column(nullable = false)
    private Integer maxConcurrentDraws;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ActivityStatus {
        DRAFT, ACTIVE, PAUSED, ENDED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ActivityStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LotteryActivity() {}

    public LotteryActivity(String name, String description, LocalDateTime startTime, 
                          LocalDateTime endTime, Integer maxDrawsPerUser, Integer maxConcurrentDraws) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxDrawsPerUser = maxDrawsPerUser;
        this.maxConcurrentDraws = maxConcurrentDraws;
        this.status = ActivityStatus.DRAFT;
    }

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

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 