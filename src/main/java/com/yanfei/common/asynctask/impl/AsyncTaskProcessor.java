package com.yanfei.common.asynctask.impl;

import com.yanfei.common.asynctask.api.AsyncTask;
import com.yanfei.common.asynctask.api.AsyncTaskSetter;
import com.yanfei.common.asynctask.db.dto.AsyncTaskDto;
import com.yanfei.common.asynctask.db.mapper.AsyncTaskMapper;
import com.yanfei.common.asynctask.entity.*;
import com.yanfei.common.asynctask.entity.AsyncTaskEntity;
import com.yanfei.common.asynctask.entity.AsyncTaskState;
import com.yanfei.common.asynctask.err.ConcurrentUpdateException;
import com.yanfei.common.asynctask.err.TaskSkippedException;
import com.yanfei.common.asynctask.util.SpringUtil;

import java.sql.Timestamp;

public class AsyncTaskProcessor {

    private AsyncTaskDto _taskDto;
    private Timestamp _lastUpdateTime;
    private String _data;
    private String _tablePostfix;

    public AsyncTaskProcessor(AsyncTaskDto task, String tablePostfix) {
        if (task == null) {
            throw new IllegalArgumentException("task");
        }
        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }
        _taskDto = task;
        _lastUpdateTime = _taskDto.updateTime;
        _data = _taskDto.data;
        _tablePostfix = tablePostfix;
    }


    public void process() throws ConcurrentUpdateException {
        int state = _taskDto.state;
        switch (state) {
            case AsyncTaskState.TO_DO:
                handle();
                break;
            case AsyncTaskState.DONE:
            case AsyncTaskState.FAILED:
            default:
                break;

        }
    }


    public void handle() throws ConcurrentUpdateException {
        incrementRetry();

        String beanName = _taskDto.type;
        AsyncTask asyncTask = (AsyncTask) SpringUtil.getBean(beanName);
        AsyncTaskMapper mapper = SpringUtil.getBean(AsyncTaskMapper.class);

        try {
            AsyncTaskEntity entity = toEntity(_taskDto);
            boolean skipped = asyncTask.skipped(entity);
            if (skipped) {
                return;
            }
            AsyncTaskProcessor.AsyncTaskSetterImpl setter = new AsyncTaskProcessor.AsyncTaskSetterImpl(this);
            boolean result = asyncTask.handle(entity, setter);
            int updated = mapper.taskDoneOrFailedWithOptiLock(_taskDto.id, result ? AsyncTaskState.DONE : AsyncTaskState.FAILED,
                    _lastUpdateTime, _tablePostfix);
            if (updated != 1) {
                throw new ConcurrentUpdateException(String.format(
                        "The async_task [%d] has been updated by other thread", _taskDto.id));
            }
            AsyncTaskLogUtility.addLog(_taskDto.id, result ? "handle.done" : "handle.failed",
                    result ? "0->1" : "0->2", _tablePostfix);
        } catch (Throwable t) {
            if (!(t instanceof TaskSkippedException)) {
                AsyncTaskLogUtility.addLog(_taskDto.id, "handle.error", t, _tablePostfix);
            }
        }
    }


    private static AsyncTaskEntity toEntity(AsyncTaskDto taskDto) {
        AsyncTaskEntity result = new AsyncTaskEntity();
        result.id = taskDto.id;
        result.type = taskDto.type;
        result.state = taskDto.state;
        result.data = taskDto.data;
        result.key = taskDto.key;
        result.index = taskDto.index;
        result.retry = taskDto.retry;
        result.createTime = taskDto.createTime;
        return result;
    }

    private void incrementRetry() throws ConcurrentUpdateException {
        AsyncTaskMapper mapper = SpringUtil.getBean(AsyncTaskMapper.class);

        int updated = mapper.incrementRetryWithOptiLock(_taskDto.id, _lastUpdateTime, _tablePostfix);
        if (updated != 1) {
            throw new ConcurrentUpdateException(String.format(
                    "The async task [%d] has been updated by other thread", _taskDto.id));
        }
        AsyncTaskDto updatedTask = mapper.getById(_taskDto.id, _tablePostfix);
        _lastUpdateTime = updatedTask.updateTime;
    }


    private static class AsyncTaskSetterImpl implements AsyncTaskSetter {
        private AsyncTaskProcessor _processor;

        public AsyncTaskSetterImpl(AsyncTaskProcessor processor) {
            assert (processor != null);
            _processor = processor;
        }

        @Override
        public void saveData(String data) throws ConcurrentUpdateException {
            AsyncTaskMapper mapper = SpringUtil.getBean(AsyncTaskMapper.class);
            Timestamp lastUpdateTime = _processor._lastUpdateTime;
            int updated = mapper.saveDataWithOptiLock(_processor._taskDto.id, data, lastUpdateTime, _processor._tablePostfix);
            if (updated != 1) {
                throw new ConcurrentUpdateException(String.format(
                        "The async task [%d] has been updated by other thread", _processor._taskDto.id));

            }
            AsyncTaskLogUtility.addLog(_processor._taskDto.id, "task.saveData",
                    String.format("%s -> %s", _processor._data, data), _processor._tablePostfix);

            AsyncTaskDto updatedTask = mapper.getById(_processor._taskDto.id, _processor._tablePostfix);
            _processor._data = data;
            _processor._lastUpdateTime = updatedTask.updateTime;
        }
    }
}
