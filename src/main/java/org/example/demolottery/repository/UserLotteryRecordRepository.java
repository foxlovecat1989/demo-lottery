package org.example.demolottery.repository;

import org.example.demolottery.entity.UserLotteryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserLotteryRecordRepository extends JpaRepository<UserLotteryRecord, Long> {
    
    @Query("SELECT COUNT(r) FROM UserLotteryRecord r WHERE r.userId = :userId AND r.activityId = :activityId")
    long countByUserIdAndActivityId(@Param("userId") String userId, @Param("activityId") Long activityId);
    
    @Query("SELECT COUNT(r) FROM UserLotteryRecord r WHERE r.userId = :userId AND r.activityId = :activityId AND r.createdAt >= :startTime")
    long countByUserIdAndActivityIdAndCreatedAtAfter(@Param("userId") String userId, 
                                                    @Param("activityId") Long activityId, 
                                                    @Param("startTime") LocalDateTime startTime);
    
    Page<UserLotteryRecord> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Page<UserLotteryRecord> findByActivityIdOrderByCreatedAtDesc(Long activityId, Pageable pageable);
    
    Page<UserLotteryRecord> findByUserIdAndActivityIdOrderByCreatedAtDesc(String userId, Long activityId, Pageable pageable);
} 