package com.yanfei.common.asynctask.entity;

import java.sql.Timestamp;

public class AsyncTaskEntity {
    public long id;
    public String type;
    public int state;
    public String key;
    public long index;
    public int retry;
    public String data;
    public Timestamp createTime;
}
