package com.yanfei.common.asynctask.impl;

import com.yanfei.common.asynctask.db.dto.AsyncTaskDto;
import com.yanfei.common.asynctask.db.mapper.AsyncTaskMapper;
import com.yanfei.common.asynctask.entity.AsyncTaskState;
import com.yanfei.common.asynctask.err.ConcurrentUpdateException;
import com.yanfei.common.asynctask.util.SpringUtil;

public class AsyncTaskProcessorStrategy {

    private AsyncTaskDto _task;
    private int _retryOnException;
    private String _tablePostfix;

    public AsyncTaskProcessorStrategy(AsyncTaskDto task, int retryOnException, String tablePostfix) {
        if (task == null) {
            throw new IllegalArgumentException("task");
        }
        if (retryOnException < 0) {
            throw new IllegalArgumentException("retryOnException");
        }
        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }
        _task = task;
        _retryOnException = retryOnException;
        _tablePostfix = tablePostfix;
    }

    public long getTaskId() {
        return _task.id;
    }

    public String getTablePostfix() {
        return _tablePostfix;
    }


    public void process() throws ConcurrentUpdateException {

        AsyncTaskDto task = _task;
        int retry = 0;

        AsyncTaskMapper mapper = SpringUtil.getBean(AsyncTaskMapper.class);

        while (true) {
            AsyncTaskProcessor p = new AsyncTaskProcessor(task, _tablePostfix);
            p.process();

            AsyncTaskDto newTask = mapper.getById(task.id, _tablePostfix);
            if (newTask.state == AsyncTaskState.DONE || newTask.state == AsyncTaskState.FAILED) {
                //finished
                break;
            } else {
                if (task.state == newTask.state) {
                    retry++;
                    if (retry > _retryOnException) {
                        break;
                    }
                } else {
                    //continue
                    retry = 0;
                }
            }
            task = newTask;
        }
    }
}
