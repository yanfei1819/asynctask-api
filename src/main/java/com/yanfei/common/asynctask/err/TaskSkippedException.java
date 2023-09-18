package com.yanfei.common.asynctask.err;

public class TaskSkippedException extends Exception {
    public TaskSkippedException(long taskId) {
        super(String.format("The async task [%d] has been skipped", taskId));
    }
}
