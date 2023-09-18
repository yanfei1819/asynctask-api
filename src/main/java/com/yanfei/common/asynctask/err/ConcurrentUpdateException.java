package com.yanfei.common.asynctask.err;

public class ConcurrentUpdateException extends Exception {
    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
