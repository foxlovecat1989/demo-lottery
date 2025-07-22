package org.example.demolottery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.distributed-lock")
public class DistributedLockConfig {
    
    public static final class LockKeys {
        public static final String PRIZE_DRAW = "prize_draw:activity:";
        public static final String USER_DRAW_COUNT = "user_draw_count:";
        public static final String ACTIVITY_STATUS = "activity_status:";
        
        private LockKeys() {}
    }
    
    public static final class TimeoutConfig {
        public static final long PRIZE_DRAW_TIMEOUT_SECONDS = 10L;
        public static final long USER_VALIDATION_TIMEOUT_SECONDS = 5L;
        public static final long ACTIVITY_UPDATE_TIMEOUT_SECONDS = 30L;
        
        private TimeoutConfig() {}
    }
    
    private boolean enabled = true;
    private long defaultTimeoutSeconds = 30L;
    private int maxRetryAttempts = 3;
    private long retryDelayMillis = 100L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDefaultTimeoutSeconds() {
        return defaultTimeoutSeconds;
    }

    public void setDefaultTimeoutSeconds(long defaultTimeoutSeconds) {
        this.defaultTimeoutSeconds = defaultTimeoutSeconds;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public long getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public void setRetryDelayMillis(long retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }
} 