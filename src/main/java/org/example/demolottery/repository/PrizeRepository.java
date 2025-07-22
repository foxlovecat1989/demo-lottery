package org.example.demolottery.repository;

import org.example.demolottery.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {
    
    List<Prize> findByActivityIdOrderBySortOrderAsc(Long activityId);
    
    List<Prize> findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(Long activityId, Integer quantity);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prize p WHERE p.id = :prizeId AND p.remainingQuantity > 0")
    Optional<Prize> findByIdWithLock(@Param("prizeId") Long prizeId);
    
    @Modifying
    @Query("UPDATE Prize p SET p.remainingQuantity = p.remainingQuantity - 1 WHERE p.id = :prizeId AND p.remainingQuantity > 0")
    int decrementRemainingQuantity(@Param("prizeId") Long prizeId);
    
    @Query("SELECT SUM(p.probability) FROM Prize p WHERE p.activityId = :activityId")
    Double getTotalProbabilityByActivityId(@Param("activityId") Long activityId);
} 