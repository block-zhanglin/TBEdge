package com.zl.blockchain.util;

import org.slf4j.LoggerFactory;

/**
 *
 */
public class LogUtil {

    /**
     *获取错误信息
     */
    public static void error(String message, Exception exception) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).error("["+stackTraceElement.getLineNumber()+"] - "+message,exception);
    }

    /**
     * 获取debug
     * @param message
     */
    public static void debug(String message) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).debug("["+stackTraceElement.getLineNumber()+"] - "+message);
    }

    /**
     *异常处理中常用StackTrace（堆栈轨迹）中的一个元素，属性包括方法调用者的类名
     * @return
     */
    private static StackTraceElement getStackTraceElement(){
        StackTraceElement[] classArray= new Exception().getStackTrace() ;
        return classArray[2];
    }

}
