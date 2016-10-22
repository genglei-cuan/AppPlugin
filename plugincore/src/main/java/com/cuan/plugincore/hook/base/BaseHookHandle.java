package com.cuan.plugincore.hook.base;

import java.util.HashMap;

/**
 * Created by genglei.cuan on 2016/10/21.
 * genglei.cuan@godinsec.com
 */

public abstract class BaseHookHandle {


    protected HashMap<String,HookedMethodHandler> mHookedMethodHandlers = new HashMap<>(5);
    /**
     * 获取用来处理被hook方法的handler
     *
     * @param methodName
     * 方法名字
     *
     * @return
     * 返回null,说明该方法没有被hook
     * 否则返回处理该方法的handler
     */
    public HookedMethodHandler getHookedMethodHandler(String methodName){
        if(methodName!= null)
            return mHookedMethodHandlers.get(methodName);
        else
            return null;
    }

    /**
     * 子类在该方法中添加中对mHookedMethodHandlers直接操作，添加hook。这样执行效率高。
     */
    protected abstract void init();

    public BaseHookHandle(){
        init();
    }


    public void addHook(String methodName,HookedMethodHandler hookMethodHandler){
        if(methodName != null && !methodName.isEmpty() && hookMethodHandler != null)
            mHookedMethodHandlers.put(methodName,hookMethodHandler);
    }

    public void removeHook(String methodName){
        if(methodName != null && !methodName.isEmpty())
            mHookedMethodHandlers.remove(methodName);
    }
}
