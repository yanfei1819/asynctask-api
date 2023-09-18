package com.yanfei.common.asynctask.db.dto;

import java.sql.Timestamp;

public class AsyncTaskDto {
    public long id;
    public String type;
    public int state;
    public String key;
    public long index;
    public int retry;
    public String data;
    public Timestamp createTime;
    public Timestamp updateTime;
}
