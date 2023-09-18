package com.yanfei.common.asynctask.impl;

import com.yanfei.common.asynctask.db.dto.AsyncTaskDto;
import com.yanfei.common.asynctask.db.mapper.AsyncTaskMapper;
import com.yanfei.common.asynctask.util.SpringUtil;
import com.yanfei.common.asynctask.util.TablePostfixUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AsyncTaskExecutorImpl {
    private static final int PAGE_SIZE = 50;
    private static final int MAX_PARALLEL = 8;

    public static void execute(String asyncTaskType, String tablePostfix) {
        executeOnce(asyncTaskType, 0, tablePostfix);
    }

    public static void execute(String asyncTaskType, int retryOnException, String tablePostfix) {
        executeOnce(asyncTaskType, retryOnException, tablePostfix);
    }

    private static void executeOnce(String asyncTaskType, int retryOnException, String tablePostfix) {
        if (asyncTaskType == null) {
            throw new IllegalArgumentException("asyncTaskType");
        }
        AsyncTaskMapper asyncTaskMapper = SpringUtil.getBean(AsyncTaskMapper.class);

        int pageIndex = 0;
        Set<Long> prevSet = new HashSet<Long>();
        String normalizedTablePostfix = TablePostfixUtil.normalize(tablePostfix);
        while (true) {
            List<AsyncTaskDto> list = asyncTaskMapper.getUndoneList(asyncTaskType, pageIndex * PAGE_SIZE,
                    PAGE_SIZE * 2, normalizedTablePostfix);
            if (list == null || list.size() == 0) {
                break;
            }

            List<AsyncTaskProcessorStrategy> toRun = new ArrayList<AsyncTaskProcessorStrategy>();
            for (AsyncTaskDto task : list) {
                if (prevSet.contains(Long.valueOf(task.id))) {
                    continue;
                }
                toRun.add(new AsyncTaskProcessorStrategy(task, retryOnException, normalizedTablePostfix));
            }

            int parallel = getParallel(toRun.size());
            AsyncTaskProcessorParellelExecutor.execute(toRun, parallel);

            if (list.size() < PAGE_SIZE * 2) {
                break;
            }
            if (allInCurrentPageHandled(list, prevSet)) {
                pageIndex++;
            }
            prevSet = toSet(list);
        }
    }


    private static boolean allInCurrentPageHandled(List<AsyncTaskDto> list, Set<Long> prevSet) {
        assert (list != null);
        assert (prevSet != null);

        for (int i = 0; i < PAGE_SIZE && i < list.size(); ++i) {
            AsyncTaskDto task = list.get(i);
            if (!prevSet.contains(Long.valueOf(task.id))) {
                return false;
            }
        }
        return true;
    }

    private static int getParallel(int n) {
        if (n <= 1) {
            return 1;
        } else if (n < 4) {
            return 2;
        } else if (n < 16) {
            return 4;
        } else {
            return MAX_PARALLEL;
        }
    }

    private static Set<Long> toSet(List<AsyncTaskDto> list) {
        assert (list != null);

        Set<Long> result = new HashSet<Long>();
        for (AsyncTaskDto task : list) {
            result.add(Long.valueOf(task.id));
        }
        return result;
    }

}
