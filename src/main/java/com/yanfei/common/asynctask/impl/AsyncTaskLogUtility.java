package com.yanfei.common.asynctask.impl;

import com.yanfei.common.asynctask.db.mapper.AsyncTaskLogMapper;
import com.yanfei.common.asynctask.util.SpringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class AsyncTaskLogUtility {
    private static final int CATEGORY_MAX_LEN = 100;
    private static final int MESSAGE_MAX_LEN = 1000;


    public static void addLog(long taskId, String category, String message, String tablePostfix) {
        if (category == null) {
            throw new IllegalArgumentException("category");
        }
        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }

        AsyncTaskLogMapper logMapper = SpringUtil.getBean(AsyncTaskLogMapper.class);
        logMapper.insertLog(taskId, trimCategory(category), trimMessage(message), tablePostfix);
    }

    public static void addLog(long taskId, String category, Throwable t, String tablePostfix) {
        if (category == null) {
            throw new IllegalArgumentException("category");
        }
        if (t == null) {
            throw new IllegalArgumentException("t");
        }
        if (tablePostfix == null) {
            throw new IllegalArgumentException("tablePostfix");
        }

        AsyncTaskLogMapper logMapper = SpringUtil.getBean(AsyncTaskLogMapper.class);
        logMapper.insertLog(taskId, trimCategory(category), trimMessage(throwableToString(t)), tablePostfix);
    }

    private static String trimCategory(String category) {
        assert (category != null);
        String result;
        if (category.length() > CATEGORY_MAX_LEN) {
            result = category.substring(0, CATEGORY_MAX_LEN);
        } else {
            result = category;
        }
        return result;
    }

    private static String trimMessage(String message) {
        String result;
        if (message == null) {
            result = "";
        } else if (message.length() > MESSAGE_MAX_LEN) {
            result = message.substring(0, MESSAGE_MAX_LEN);
        } else {
            result = message;
        }
        return result;
    }

    private static String throwableToString(Throwable t) {
        assert (t != null);
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);
        t.printStackTrace(printer);
        printer.close();
        writer.flush();
        return writer.getBuffer().toString();
    }
}
