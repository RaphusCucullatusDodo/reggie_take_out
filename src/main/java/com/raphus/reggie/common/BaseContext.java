package com.raphus.reggie.common;

/**
 * 存放同线程数据
 */
public class BaseContext {
    //threadLocal是线程隔离的
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

}
