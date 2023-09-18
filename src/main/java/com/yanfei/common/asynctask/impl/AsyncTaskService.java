package com.yanfei.common.asynctask.impl;

import com.yanfei.common.asynctask.api.AsyncTaskBuilder;
import com.yanfei.common.asynctask.db.dto.AsyncTaskDto;
import com.yanfei.common.asynctask.db.mapper.AsyncTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncTaskService {

    @Autowired
    AsyncTaskMapper _mapper;


    @Transactional(propagation = Propagation.REQUIRED)
    public void create(AsyncTaskBuilder.Task task, String tablePostfix) {
        if (task == null) {
            throw new IllegalArgumentException("task");
        }

        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }

        AsyncTaskDto asyncTaskDto = toTaskDto(task);
        _mapper.insert(asyncTaskDto, tablePostfix);
        assert (asyncTaskDto.id > 0);
    }

    public boolean exists(String type, String key, long index, String tablePostfix) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (key == null) {
            throw new IllegalArgumentException("key");
        }
        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }
        int count = _mapper.count(type, key, index, tablePostfix);
        return count > 0;
    }


    private static AsyncTaskDto toTaskDto(AsyncTaskBuilder.Task task) {
        assert (task != null);
        AsyncTaskDto result = new AsyncTaskDto();
        result.type = task.type;
        result.key = task.key;
        result.index = task.index;
        result.data = (task.data == null ? "" : task.data);
        return result;
    }
}
