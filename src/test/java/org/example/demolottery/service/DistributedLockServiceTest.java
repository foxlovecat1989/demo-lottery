package org.example.demolottery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributedLockServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private DistributedLockService distributedLockService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        distributedLockService = new DistributedLockService(redisTemplate);
    }

    @Test
    void testSuccessfulLockAcquisitionAndRelease() {
        String lockKey = "test-lock";
        
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString()))
                .thenReturn(1L);

        AtomicInteger counter = new AtomicInteger(0);
        
        String result = distributedLockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
            counter.incrementAndGet();
            return "success";
        });

        assertEquals("success", result);
        assertEquals(1, counter.get());
        
        verify(valueOperations).setIfAbsent(contains(lockKey), anyString(), eq(10L), eq(TimeUnit.SECONDS));
        verify(redisTemplate).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }

    @Test
    void testLockAcquisitionFailure() {
        String lockKey = "test-lock";
        
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            distributedLockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
                return "should not execute";
            });
        });

        assertTrue(exception.getMessage().contains("Failed to acquire distributed lock"));
        verify(valueOperations).setIfAbsent(contains(lockKey), anyString(), eq(10L), eq(TimeUnit.SECONDS));
        verify(redisTemplate, never()).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }

    @Test
    void testConcurrentLockAccess() throws InterruptedException {
        String lockKey = "concurrent-test";
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true) // First call succeeds
                .thenReturn(false) // Subsequent calls fail
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false);
        
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString()))
                .thenReturn(1L);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    distributedLockService.executeWithLock(lockKey, 1, TimeUnit.SECONDS, () -> {
                        successCount.incrementAndGet();
                        try {
                            Thread.sleep(100); // Simulate work
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    });
                } catch (RuntimeException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        
        assertEquals(1, successCount.get()); // Only one thread should succeed
        assertEquals(9, failureCount.get()); // Nine threads should fail
        
        executor.shutdown();
    }

    @Test
    void testRunnableExecution() {
        String lockKey = "runnable-test";
        
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString()))
                .thenReturn(1L);

        AtomicInteger counter = new AtomicInteger(0);
        
        assertDoesNotThrow(() -> {
            distributedLockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
                counter.incrementAndGet();
            });
        });

        assertEquals(1, counter.get());
        verify(valueOperations).setIfAbsent(contains(lockKey), anyString(), eq(10L), eq(TimeUnit.SECONDS));
        verify(redisTemplate).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }

    @Test
    void testExceptionHandlingInLockedOperation() {
        String lockKey = "exception-test";
        
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), anyString()))
                .thenReturn(1L);

        RuntimeException testException = new RuntimeException("Test exception");
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            distributedLockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
                throw testException;
            });
        });

        assertEquals(testException, thrown);
        
        // Verify lock was still released even after exception
        verify(redisTemplate).execute(any(DefaultRedisScript.class), anyList(), anyString());
    }
} 