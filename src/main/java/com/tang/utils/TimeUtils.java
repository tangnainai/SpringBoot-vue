package com.tang.utils;

import cn.hutool.core.date.format.FastDateFormat;

/**
 * @author tang
 * @date 2022/6/1 10:32
 * @desc
 */
public abstract class TimeUtils {
    public static String format(Long timestamp,String pattern){
        return FastDateFormat.getInstance(pattern).format(timestamp);
    }
}
