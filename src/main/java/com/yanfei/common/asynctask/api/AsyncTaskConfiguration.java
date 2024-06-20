package com.yanfei.common.asynctask.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = {"com.yanfei.common.asynctask.db.mapper"})
@ComponentScan(value = {"com.yanfei.common.asynctask"})
public class AsyncTaskConfiguration {

}
