package com.itheima.mall.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    /**
     * 获取值
     *
     * @return
     */
    public static Object getCurrentData() {
        return threadLocal.get();
    }

    /**
     * 设置值
     *
     * @param o
     */
    public static void setCurrentData(Object o) {
        threadLocal.set(o);
    }
}
//4