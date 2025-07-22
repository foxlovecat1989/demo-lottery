package org.example.demolottery.repository;

import org.example.demolottery.entity.LotteryActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotteryActivityRepository extends JpaRepository<LotteryActivity, Long> {
    
    List<LotteryActivity> findByStatusOrderByCreatedAtDesc(LotteryActivity.ActivityStatus status);
    
    @Query("SELECT a FROM LotteryActivity a WHERE a.status = :status AND a.startTime <= :now AND a.endTime >= :now")
    List<LotteryActivity> findActiveActivities(@Param("status") LotteryActivity.ActivityStatus status, 
                                              @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM LotteryActivity a WHERE a.id = :id AND a.status = :status AND a.startTime <= :now AND a.endTime >= :now")
    Optional<LotteryActivity> findActiveActivityById(@Param("id") Long id, 
                                                    @Param("status") LotteryActivity.ActivityStatus status, 
                                                    @Param("now") LocalDateTime now);
} 