package com.yanfei.common.asynctask.api;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(AsyncTaskConfiguration.class)
@Documented
public @interface EnableAsyncTask {

}
