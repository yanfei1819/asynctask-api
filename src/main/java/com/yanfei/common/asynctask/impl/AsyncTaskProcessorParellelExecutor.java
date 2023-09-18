package com.yanfei.common.asynctask.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class AsyncTaskProcessorParellelExecutor {

    private static final Logger _logger = LogManager.getLogger(AsyncTaskProcessorParellelExecutor.class);


    public static void execute(List<AsyncTaskProcessorStrategy> list, int parallel) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (parallel <= 0) {
            throw new IllegalArgumentException("parallel");
        }

        if (parallel > 1) {
            ForkJoinPool pool = new ForkJoinPool(parallel);
            for (AsyncTaskProcessorStrategy p : list) {
                Task task = new Task(p);
                pool.submit(task);
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            for (AsyncTaskProcessorStrategy p : list) {
                try {
                    p.process();
                } catch (Exception e) {
                    addErrorLog(p.getTaskId(), e, p.getTablePostfix());
                }
            }
        }

    }

    private static class Task implements Runnable {
        private AsyncTaskProcessorStrategy _p;

        public Task(AsyncTaskProcessorStrategy p) {
            assert (p != null);
            _p = p;
        }

        @Override
        public void run() {
            try {
                _p.process();
            } catch (Exception e) {
                addErrorLog(_p.getTaskId(), e, _p.getTablePostfix());
            }
        }
    }

    private static void addErrorLog(long taskId, Exception e, String tablePostfix) {
        try {
            assert (tablePostfix != null);
            AsyncTaskLogUtility.addLog(taskId, "handle.error", e, tablePostfix);
        } catch (Exception ex) {
            _logger.error(ex);
        }
    }
}
