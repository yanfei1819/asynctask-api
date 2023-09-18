package com.yanfei.common.asynctask.db.mapper;

import com.yanfei.common.asynctask.db.dto.AsyncTaskDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AsyncTaskMapper {

    @Insert("insert into async_task${tablePostfix}(type, state, `key`, `index`, data, retry) values(#{t.type},0, #{t.key}, #{t.index}, #{t.data}, 0)")
    @SelectKey(resultType = long.class, keyColumn = "id", before = false, statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "t.id")
    int insert(@Param("t") AsyncTaskDto task, @Param("tablePostfix") String tablePostfix);

    @Select("select count(*) from async_task${tablePostfix} where type=#{type} and `key`=#{key} and `index`=#{index}")
    int count(@Param("type") String type, @Param("key") String key, @Param("index") long index, @Param("tablePostfix") String tablePostfix);


    @Select("select id, type, state, `key`, `index`, data, retry, createTime, updateTime from async_task${tablePostfix} where type=#{type} and state=0 limit ${start},${count}")
    List<AsyncTaskDto> getUndoneList(@Param("type") String type, @Param("start") int start, @Param("count") int count, @Param("tablePostfix") String tablePostfix);

    @Update("update async_task${tablePostfix} set data=#{data} where id=#{id} and updateTime=#{updateTime}")
    int saveDataWithOptiLock(@Param("id") long id, @Param("data") String data, @Param("updateTime") Timestamp updateTime, @Param("tablePostfix") String tablePostfix);

    @Select("select id, type, state, `key`, `index`, data, retry, createTime, updateTime from async_task${tablePostfix} where id=#{id}")
    AsyncTaskDto getById(@Param("id") long id, @Param("tablePostfix") String tablePostfix);

    @Update("update async_task${tablePostfix} set state=${state} where id=#{id} and updateTime=#{updateTime} and state=0")
    int taskDoneOrFailedWithOptiLock(@Param("id") long id, @Param("state") int state, @Param("updateTime") Timestamp updateTime, @Param("tablePostfix") String tablePostfix);

    @Update("update async_task${tablePostfix} set retry=retry+1 where id=#{id} and updateTime=#{updateTime}")
    int incrementRetryWithOptiLock(@Param("id") long id, @Param("updateTime") Timestamp updateTime, @Param("tablePostfix") String tablePostfix);


}
