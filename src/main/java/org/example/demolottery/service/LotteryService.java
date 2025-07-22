package org.example.demolottery.service;

import org.example.demolottery.config.DistributedLockConfig;
import org.example.demolottery.dto.request.LotteryDrawRequest;
import org.example.demolottery.dto.response.LotteryDrawResponse;
import org.example.demolottery.entity.LotteryActivity;
import org.example.demolottery.entity.Prize;
import org.example.demolottery.entity.UserLotteryRecord;
import org.example.demolottery.exception.LotteryException;
import org.example.demolottery.repository.LotteryActivityRepository;
import org.example.demolottery.repository.PrizeRepository;
import org.example.demolottery.repository.UserLotteryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LotteryService {

    private final LotteryActivityRepository activityRepository;
    private final PrizeRepository prizeRepository;
    private final UserLotteryRecordRepository recordRepository;
    private final ProbabilityCalculationService probabilityService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DistributedLockService distributedLockService;

    @Value("${app.distributed-lock.enabled:false}")
    private boolean distributedLockEnabled;

    private static final String ACTIVITY_CONCURRENT_KEY = "lottery:concurrent:activity:";

    public LotteryService(LotteryActivityRepository activityRepository,
                         PrizeRepository prizeRepository,
                         UserLotteryRecordRepository recordRepository,
                         ProbabilityCalculationService probabilityService,
                         @Autowired(required = false) RedisTemplate<String, Object> redisTemplate,
                         @Autowired(required = false) DistributedLockService distributedLockService) {
        this.activityRepository = activityRepository;
        this.prizeRepository = prizeRepository;
        this.recordRepository = recordRepository;
        this.probabilityService = probabilityService;
        this.redisTemplate = redisTemplate;
        this.distributedLockService = distributedLockService;
    }

    @Transactional
    public LotteryDrawResponse performDraw(String userId, LotteryDrawRequest request) {
        LotteryActivity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new LotteryException("Activity not found"));

        validateActivity(activity);
        validateUserDrawPermission(userId, activity, request.getDrawCount());

        String batchId = UUID.randomUUID().toString();
        List<LotteryDrawResponse.DrawResult> results = new ArrayList<>();

        for (int i = 0; i < request.getDrawCount(); i++) {
            LotteryDrawResponse.DrawResult result = performSingleDraw(userId, activity, batchId, i + 1);
            results.add(result);
        }

        return new LotteryDrawResponse(batchId, activity.getId(), activity.getName(), 
                                     request.getDrawCount(), results, LocalDateTime.now());
    }

    public long getUserDrawCount(String userId, Long activityId) {
        return recordRepository.countByUserIdAndActivityId(userId, activityId);
    }

    private void validateActivity(LotteryActivity activity) {
        if (!activity.getStatus().equals(LotteryActivity.ActivityStatus.ACTIVE)) {
            throw new LotteryException("Activity is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new LotteryException("Activity is not within valid time range");
        }
    }

    private LotteryDrawResponse.DrawResult performSingleDraw(String userId, LotteryActivity activity,
                                                           String batchId, Integer drawIndex) {
        if (distributedLockEnabled && distributedLockService != null) {
            String prizeLockKey = DistributedLockConfig.LockKeys.PRIZE_DRAW + activity.getId();
            
            return distributedLockService.executeWithLock(prizeLockKey, 
                    DistributedLockConfig.TimeoutConfig.PRIZE_DRAW_TIMEOUT_SECONDS, TimeUnit.SECONDS, () -> {
                return performDrawLogic(userId, activity, batchId, drawIndex);
            });
        } else {
            // For development without Redis/distributed locks
            return performDrawLogic(userId, activity, batchId, drawIndex);
        }
    }

    private LotteryDrawResponse.DrawResult performDrawLogic(String userId, LotteryActivity activity,
                                                          String batchId, Integer drawIndex) {
        List<Prize> availablePrizes = prizeRepository
                .findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(activity.getId(), 0);
        
        Prize wonPrize = probabilityService.calculateWinningPrize(availablePrizes);
        
        if (wonPrize != null) {
            // Use pessimistic lock for the specific prize being decremented
            Optional<Prize> lockedPrizeOptional = prizeRepository.findByIdWithLock(wonPrize.getId());
            if (lockedPrizeOptional.isEmpty()) {
                // Prize might have been exhausted by another concurrent transaction
                return saveAndCreateNoPrizeResult(userId, activity, batchId, drawIndex);
            }
            Prize lockedPrize = lockedPrizeOptional.get();
            if (lockedPrize.getRemainingQuantity() <= 0) {
                return saveAndCreateNoPrizeResult(userId, activity, batchId, drawIndex);
            }

            int updated = prizeRepository.decrementRemainingQuantity(lockedPrize.getId());
            if (updated == 0) {
                return saveAndCreateNoPrizeResult(userId, activity, batchId, drawIndex);
            }
            
            recordRepository.save(new UserLotteryRecord(userId, activity.getId(), batchId, 
                    lockedPrize.getId(), lockedPrize.getName(), UserLotteryRecord.DrawResult.WON));
            return new LotteryDrawResponse.DrawResult(drawIndex, true, lockedPrize.getId(), 
                    lockedPrize.getName(), lockedPrize.getDescription(), lockedPrize.getImageUrl());
        } else {
            return saveAndCreateNoPrizeResult(userId, activity, batchId, drawIndex);
        }
    }

    private LotteryDrawResponse.DrawResult saveAndCreateNoPrizeResult(String userId, LotteryActivity activity,
                                                                    String batchId, Integer drawIndex) {
        recordRepository.save(new UserLotteryRecord(userId, activity.getId(), batchId, 
                null, "No Prize", UserLotteryRecord.DrawResult.NO_PRIZE));
        return createNoPrizeResult(userId, activity, batchId, drawIndex);
    }

    private LotteryDrawResponse.DrawResult createNoPrizeResult(String userId, LotteryActivity activity,
                                                             String batchId, Integer drawIndex) {
        return new LotteryDrawResponse.DrawResult(drawIndex, false);
    }

    private void validateUserDrawPermission(String userId, LotteryActivity activity, Integer drawCount) {
        if (distributedLockEnabled && distributedLockService != null) {
            String userLockKey = DistributedLockConfig.LockKeys.USER_DRAW_COUNT + userId + ":activity:" + activity.getId();
            
            distributedLockService.executeWithLock(userLockKey, 
                    DistributedLockConfig.TimeoutConfig.USER_VALIDATION_TIMEOUT_SECONDS, TimeUnit.SECONDS, () -> {
                performUserValidation(userId, activity, drawCount);
            });
        } else {
            // For development without Redis/distributed locks
            performUserValidation(userId, activity, drawCount);
        }
    }

    private void performUserValidation(String userId, LotteryActivity activity, Integer drawCount) {
        long existingDraws = recordRepository.countByUserIdAndActivityId(userId, activity.getId());
        
        if (existingDraws + drawCount > activity.getMaxDrawsPerUser()) {
            throw new LotteryException("Draw count exceeds maximum allowed per user");
        }

        if (distributedLockEnabled && redisTemplate != null) {
            String currentConcurrentKey = ACTIVITY_CONCURRENT_KEY + activity.getId();
            Long currentConcurrent = redisTemplate.opsForSet().size(currentConcurrentKey);
            if (currentConcurrent != null && currentConcurrent >= activity.getMaxConcurrentDraws()) {
                throw new LotteryException("Too many concurrent draws. Please try again later.");
            }
        }
        // Skip concurrent validation when Redis is not available
    }
} 