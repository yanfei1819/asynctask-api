package com.yanfei.common.asynctask.api;

import com.yanfei.common.asynctask.impl.AsyncTaskService;
import com.yanfei.common.asynctask.util.SpringUtil;
import com.yanfei.common.asynctask.util.TablePostfixUtil;


public class AsyncTaskBuilder {

    private String _type;
    private String _data;
    private String _key;
    private long _index;
    private String _tablePostfix;

    public AsyncTaskBuilder(String type, String key, long index) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (key == null) {
            throw new IllegalArgumentException("key");
        }
        _type = type;
        _key = key;
        _index = index;
        _tablePostfix = "";
    }

    public AsyncTaskBuilder(String type, String key) {
        this(type, key, 0);
    }

    public AsyncTaskBuilder data(String data) {
        _data = data == null ? "" : data;
        return this;
    }

    public AsyncTaskBuilder tablePostfix(String tablePostfix) {
        _tablePostfix = TablePostfixUtil.normalize(tablePostfix);
        return this;
    }


    public void create() {
        AsyncTaskService asyncTaskService = SpringUtil.getBean(AsyncTaskService.class);
        asyncTaskService.create(toTask(), _tablePostfix);
    }

    public boolean createIfNotExists() {
        if (exists()) {
            return false;
        }
        create();
        return true;
    }

    public boolean exists() {
        AsyncTaskService asyncTaskService = SpringUtil.getBean(AsyncTaskService.class);
        return asyncTaskService.exists(_type, _key, _index, _tablePostfix);
    }


    public AsyncTaskBuilder.Task toTask() {
        AsyncTaskBuilder.Task result = new AsyncTaskBuilder.Task();
        result.type = _type;
        result.key = _key;
        result.index = _index;
        result.data = _data;
        return result;
    }

    public static class Task {
        public String type;
        public String key;
        public long index;
        public String data;
    }

}
