package com.yanfei.common.asynctask.api;

import com.yanfei.common.asynctask.entity.AsyncTaskEntity;

public interface AsyncTask {
    boolean handle(AsyncTaskEntity entity, AsyncTaskSetter setter) throws Throwable;
    boolean skipped(AsyncTaskEntity entity);
}
