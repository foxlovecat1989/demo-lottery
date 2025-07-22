package org.example.demolottery.service;

import org.example.demolottery.dto.request.LotteryDrawRequest;
import org.example.demolottery.dto.response.LotteryDrawResponse;
import org.example.demolottery.entity.LotteryActivity;
import org.example.demolottery.entity.Prize;
import org.example.demolottery.entity.UserLotteryRecord;
import org.example.demolottery.exception.LotteryException;
import org.example.demolottery.repository.LotteryActivityRepository;
import org.example.demolottery.repository.PrizeRepository;
import org.example.demolottery.repository.UserLotteryRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LotteryServiceTest {

    @Mock
    private LotteryActivityRepository activityRepository;

    @Mock
    private PrizeRepository prizeRepository;

    @Mock
    private UserLotteryRecordRepository recordRepository;

    @Mock
    private ProbabilityCalculationService probabilityService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private DistributedLockService distributedLockService;

    private LotteryService lotteryService;

    private LotteryActivity testActivity;
    private Prize testPrize;
    private List<Prize> testPrizes;

    @BeforeEach
    void setUp() {
        lotteryService = new LotteryService(activityRepository, prizeRepository, recordRepository,
                probabilityService, redisTemplate, distributedLockService);

        testActivity = new LotteryActivity();
        testActivity.setId(1L);
        testActivity.setName("Test Activity");
        testActivity.setStatus(LotteryActivity.ActivityStatus.ACTIVE);
        testActivity.setStartTime(LocalDateTime.now().minusHours(1));
        testActivity.setEndTime(LocalDateTime.now().plusHours(1));
        testActivity.setMaxDrawsPerUser(10);
        testActivity.setMaxConcurrentDraws(100);

        testPrize = new Prize();
        testPrize.setId(1L);
        testPrize.setName("Test Prize");
        testPrize.setDescription("Test Description");
        testPrize.setImageUrl("test.jpg");
        testPrize.setProbability(BigDecimal.valueOf(10.0));
        testPrize.setTotalQuantity(100);
        testPrize.setRemainingQuantity(50);

        testPrizes = Arrays.asList(testPrize);

        doAnswer(invocation -> {
            Runnable action = invocation.getArgument(3);
            action.run();
            return null;
        }).when(distributedLockService).executeWithLock(anyString(), anyLong(), any(), any(Runnable.class));
        
        doAnswer(invocation -> {
            java.util.function.Supplier<?> action = invocation.getArgument(3);
            return action.get();
        }).when(distributedLockService).executeWithLock(anyString(), anyLong(), any(), any(java.util.function.Supplier.class));
    }

    @Test
    void testSuccessfulSingleDraw() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(0L);
        when(prizeRepository.findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(1L, 0))
                .thenReturn(testPrizes);
        when(probabilityService.calculateWinningPrize(testPrizes)).thenReturn(testPrize);
        when(prizeRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testPrize));
        when(prizeRepository.decrementRemainingQuantity(1L)).thenReturn(1);

        LotteryDrawResponse response = lotteryService.performDraw("user1", request);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        assertTrue(response.getResults().get(0).getWon());
        assertEquals("Test Prize", response.getResults().get(0).getPrizeName());

        verify(recordRepository).save(any(UserLotteryRecord.class));
    }

    @Test
    void testMultipleDraw() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 3);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(0L);
        when(prizeRepository.findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(1L, 0))
                .thenReturn(testPrizes);
        when(prizeRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testPrize)).thenReturn(Optional.of(testPrize));
        when(probabilityService.calculateWinningPrize(testPrizes))
                .thenReturn(testPrize)
                .thenReturn(null)
                .thenReturn(testPrize);
        when(prizeRepository.decrementRemainingQuantity(1L)).thenReturn(1).thenReturn(1).thenReturn(1);

        LotteryDrawResponse response = lotteryService.performDraw("user1", request);

        assertNotNull(response);
        assertEquals(3, response.getResults().size());
        assertTrue(response.getResults().get(0).getWon());
        assertFalse(response.getResults().get(1).getWon());
        assertTrue(response.getResults().get(2).getWon());

        verify(recordRepository, times(3)).save(any(UserLotteryRecord.class));
    }

    @Test
    void testActivityNotFound() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);

        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LotteryException.class, () -> {
            lotteryService.performDraw("user1", request);
        });
    }

    @Test
    void testInactiveActivity() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);
        testActivity.setStatus(LotteryActivity.ActivityStatus.PAUSED);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        assertThrows(LotteryException.class, () -> {
            lotteryService.performDraw("user1", request);
        });
    }

    @Test
    void testExceedMaxDrawsPerUser() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(10L);

        assertThrows(LotteryException.class, () -> {
            lotteryService.performDraw("user1", request);
        });
    }

    @Test
    void testNoPrizeAvailable() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(0L);
        when(prizeRepository.findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(1L, 0))
                .thenReturn(Collections.emptyList());
        when(probabilityService.calculateWinningPrize(Collections.emptyList())).thenReturn(null);

        LotteryDrawResponse response = lotteryService.performDraw("user1", request);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        assertFalse(response.getResults().get(0).getWon());

        verify(recordRepository).save(any(UserLotteryRecord.class));
    }

    @Test
    void testPrizeExhausted() {
        LotteryDrawRequest request = new LotteryDrawRequest(1L, 1);
        testPrize.setRemainingQuantity(0);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(0L);
        when(prizeRepository.findByActivityIdAndRemainingQuantityGreaterThanOrderBySortOrderAsc(1L, 0))
                .thenReturn(testPrizes);
        when(probabilityService.calculateWinningPrize(testPrizes)).thenReturn(testPrize);
        when(prizeRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testPrize));
        when(prizeRepository.decrementRemainingQuantity(1L)).thenReturn(0);

        LotteryDrawResponse response = lotteryService.performDraw("user1", request);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        assertFalse(response.getResults().get(0).getWon());

        verify(recordRepository, times(1)).save(argThat(record -> 
            record.getUserId().equals("user1") &&
            record.getActivityId().equals(1L) &&
            record.getResult().equals(UserLotteryRecord.DrawResult.NO_PRIZE)
        ));
    }

    @Test
    void testGetUserDrawCount() {
        when(recordRepository.countByUserIdAndActivityId("user1", 1L)).thenReturn(5L);

        long count = lotteryService.getUserDrawCount("user1", 1L);

        assertEquals(5L, count);
        verify(recordRepository).countByUserIdAndActivityId("user1", 1L);
    }
} 