package com.cuan.plugincore.hook.proxy;

import android.app.ActivityThread;
import android.os.Handler;

import com.cuan.helper.log.DLog;
import com.cuan.helper.reflect.FieldUtils;
import com.cuan.plugincore.hook.base.BaseHookHandle;
import com.cuan.plugincore.hook.base.Hook;
import com.cuan.plugincore.hook.handle.PluginCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by genglei.cuan on 2016/10/23.
 * genglei.cuan@godinsec.com
 */

public class PluginCallbackHook extends Hook{


    private static String TAG = "PluginCallbackHook";

    private List<PluginCallback> mCallbacks = new ArrayList<PluginCallback>(1);

    @Override
    public void setEnable(boolean mEnable) {
        for(PluginCallback callback : mCallbacks){
            callback.setEnable(mEnable);
        }
        super.setEnable(mEnable);
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return null;
    }

    @Override
    public void onInstall(ClassLoader classLoader) throws Throwable {
        ActivityThread target = ActivityThread.currentActivityThread();
        Class ActivityThreadClass = Class.forName("android.app.ActivityThread");
        /*替换ActivityThread.mH.mCallback，拦截组件调度消息*/
        Field mHField = FieldUtils.getField(ActivityThreadClass, "mH");
        Handler handler = (Handler) FieldUtils.readField(mHField, target);
        Field mCallbackField = FieldUtils.getField(Handler.class, "mCallback");
        //*这里读取出旧的callback并处理*/
        Object mCallback = FieldUtils.readField(mCallbackField, handler);
        if (!PluginCallback.class.isInstance(mCallback)) {
            PluginCallback value = mCallback != null ? new PluginCallback((Handler.Callback) mCallback) : new PluginCallback(null);
            value.setEnable(isEnable());
            mCallbacks.add(value);
            FieldUtils.writeField(mCallbackField, handler, value);
            DLog.i(TAG, "PluginCallbackHook has installed");
        } else {
            DLog.i(TAG, "PluginCallbackHook has installed,skip");
        }

    }

    @Override
    public void onUnInstall(ClassLoader classLoader) throws Throwable {

    }
}
