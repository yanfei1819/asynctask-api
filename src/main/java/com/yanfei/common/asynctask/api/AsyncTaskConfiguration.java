package com.yanfei.common.asynctask.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = {"com.centchain.trgroup.common.asynctask.db.mapper"})
@ComponentScan(value = {"com.centchain.trgroup.common.asynctask"})
public class AsyncTaskConfiguration {

}
