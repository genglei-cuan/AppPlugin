package com.cuan.plugincore.hook.base;

/**
 * Created by genglei.cuan on 2016/10/21.
 * genglei.cuan@godinsec.com
 */

import com.cuan.helper.log.DLog;

import java.lang.reflect.Method;


/**
 * 处理一个被hook的方法的执行过程
 */
public abstract class HookedMethodHandler {

    private static String TAG  = "HookMethodHandler";

    protected Object mFakedResult = null;

    protected boolean mUseFakedResult = false;

    protected  boolean beforeInvoke(Object receiver, Method method,Object[] args) throws Throwable {
        return false;
    }

    protected void afterInvoke(Object receiver,Method method,Object[] args,Object invokeResult) throws Throwable {

    }

    /**
     * 被hook的方法的执行过程，其中的一些变量需要在其子类的实现方法中赋值。
     * TODO:考虑去掉计算hook方法执行的时间，以提高效率
     * @param receiver
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public synchronized Object doHookInner(Object receiver,Method method,Object[] args) throws Throwable{
        long b = System.currentTimeMillis();
        try {
            mUseFakedResult = false;
            mFakedResult = null;
            boolean suc = beforeInvoke(receiver, method, args);
            Object invokeResult = null;
            if (!suc) {
                invokeResult = method.invoke(receiver, args);
                afterInvoke(receiver, method, args, invokeResult);
            }
            if (mUseFakedResult) {
                return mFakedResult;
            } else {
                return invokeResult;
            }
        } finally {
            long time = System.currentTimeMillis() - b;
            if (time > 5) {
                DLog.i(TAG, "doHookInner method(%s.%s) cost %s ms", method.getDeclaringClass().getName(), method.getName(), time);
            }
        }
    }
}
