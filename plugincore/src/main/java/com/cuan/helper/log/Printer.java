package com.cuan.helper.log;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */
public interface Printer {
    void d(StackTraceElement element, String message, Object... args);

    void v(StackTraceElement element, String message, Object... args);

    void a(StackTraceElement element, String message, Object... args);

    void i(StackTraceElement element, String message, Object... args);

    void e(StackTraceElement element, String message, Object... args);

    void w(StackTraceElement element, String message, Object... args);

    void json(StackTraceElement element, String message);

    void object(StackTraceElement element, Object object);
}
