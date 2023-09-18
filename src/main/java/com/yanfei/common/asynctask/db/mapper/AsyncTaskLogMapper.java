package com.yanfei.common.asynctask.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AsyncTaskLogMapper {
    @Insert("insert into async_task_log${tablePostfix}(taskId, category, message) values(#{taskId}, #{category}, #{message})")
    int insertLog(@Param("taskId") long taskId, @Param("category") String category, @Param("message") String message, @Param("tablePostfix") String tablePostfix);
}
