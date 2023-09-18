package com.yanfei.common.asynctask.util;

import org.springframework.util.StringUtils;

public final class TablePostfixUtil {
    public static String normalize(String tablePostfix) {
        String result;
        if (StringUtils.isEmpty(tablePostfix)) {
            result = "";
        } else if (tablePostfix.startsWith("_")) {
            result = tablePostfix;
        } else {
            result = String.format("_%s", tablePostfix);
        }
        return result;
    }
}
