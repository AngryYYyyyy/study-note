package com.boge.logger;

import org.apache.ibatis.logging.Log;

/**
 * 自定义的日志实现类
 */
public class MyLogger implements Log {
    public MyLogger(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        System.err.println("【"+s+"】");
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        System.err.println("【"+s+"】");
    }

    @Override
    public void debug(String s) {
        System.err.println("【"+s+"】");
    }

    @Override
    public void trace(String s) {
        System.err.println("【"+s+"】");
    }

    @Override
    public void warn(String s) {
        System.err.println("【"+s+"】");
    }
}
