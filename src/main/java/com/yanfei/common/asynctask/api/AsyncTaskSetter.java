package com.yanfei.common.asynctask.api;

import com.yanfei.common.asynctask.err.ConcurrentUpdateException;

public interface AsyncTaskSetter {
    void saveData(String data) throws ConcurrentUpdateException;
}
