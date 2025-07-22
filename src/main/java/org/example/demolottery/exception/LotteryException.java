package org.example.demolottery.exception;

public class LotteryException extends RuntimeException {
    
    public LotteryException(String message) {
        super(message);
    }
    
    public LotteryException(String message, Throwable cause) {
        super(message, cause);
    }
} 