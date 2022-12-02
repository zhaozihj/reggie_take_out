package com.itheima.reggie.common;

/*
基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    //因为要用它存储id，id是long型所以这个泛型是Long
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    //保存id为线程局部变量
    public static void setCurrentId(Long id){
        threadLocal.set(id);

    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
