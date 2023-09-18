package com.yanfei.common.asynctask.api;

import com.yanfei.common.asynctask.impl.AsyncTaskExecutorImpl;

public final class AsyncTaskExecutor {
    public static void execute(String asyncTaskType) {
        AsyncTaskExecutorImpl.execute(asyncTaskType, "");
    }

    public static void execute(String asyncTaskType, int retryOnException) {
        AsyncTaskExecutorImpl.execute(asyncTaskType, retryOnException, "");
    }

    public static void execute(String asyncTaskType, String tablePostfix) {
        AsyncTaskExecutorImpl.execute(asyncTaskType, tablePostfix);
    }

    public static void execute(String asyncTaskType, int retryOnException, String tablePostfix) {
        AsyncTaskExecutorImpl.execute(asyncTaskType, retryOnException, tablePostfix);
    }
}
