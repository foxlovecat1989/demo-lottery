package org.example.demolottery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class DistributedLockService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockService.class);
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOCK_PREFIX = "lottery:lock:";
    private static final String UNLOCK_LUA_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";
    
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>();
    
    static {
        UNLOCK_SCRIPT.setScriptText(UNLOCK_LUA_SCRIPT);
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public DistributedLockService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> T executeWithLock(String lockKey, long timeout, TimeUnit timeUnit, Supplier<T> action) {
        String lockValue = UUID.randomUUID().toString();
        String fullLockKey = LOCK_PREFIX + lockKey;
        
        boolean acquired = false;
        try {
            acquired = acquireLock(fullLockKey, lockValue, timeout, timeUnit);
            if (!acquired) {
                throw new RuntimeException("Failed to acquire distributed lock: " + lockKey);
            }
            
            logger.debug("Acquired distributed lock: {}", lockKey);
            return action.get();
            
        } finally {
            if (acquired) {
                releaseLock(fullLockKey, lockValue);
                logger.debug("Released distributed lock: {}", lockKey);
            }
        }
    }

    public void executeWithLock(String lockKey, long timeout, TimeUnit timeUnit, Runnable action) {
        executeWithLock(lockKey, timeout, timeUnit, () -> {
            action.run();
            return null;
        });
    }

    private boolean acquireLock(String lockKey, String lockValue, long timeout, TimeUnit timeUnit) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, timeUnit);
        return Boolean.TRUE.equals(result);
    }

    private boolean releaseLock(String lockKey, String lockValue) {
        Long result = redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        return result != null && result > 0;
    }

    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit timeUnit) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        return acquireLock(fullLockKey, lockValue, timeout, timeUnit);
    }

    public boolean releaseLockPublic(String lockKey, String lockValue) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        return releaseLock(fullLockKey, lockValue);
    }
} 