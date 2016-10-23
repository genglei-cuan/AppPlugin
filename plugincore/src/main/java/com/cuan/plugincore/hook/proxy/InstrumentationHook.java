package com.cuan.plugincore.hook.proxy;

import android.app.ActivityThread;
import android.app.Instrumentation;

import com.cuan.helper.log.DLog;
import com.cuan.helper.reflect.FieldUtils;
import com.cuan.plugincore.hook.base.BaseHookHandle;
import com.cuan.plugincore.hook.base.Hook;
import com.cuan.plugincore.hook.handle.PluginInstrumentation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by genglei.cuan on 2016/10/23.
 * genglei.cuan@godinsec.com
 */

public class InstrumentationHook extends Hook {

    private static String TAG = "InstrumentationHook";
    private List<PluginInstrumentation> mPluginInstrumentations = new ArrayList<PluginInstrumentation>();


    @Override
    public void setEnable(boolean mEnable) {
        for(PluginInstrumentation instrumentation : mPluginInstrumentations){
            instrumentation.setEnable(mEnable);
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

         /*替换ActivityThread.mInstrumentation，拦截组件调度消息*/
        Field mInstrumentationField = FieldUtils.getField(ActivityThreadClass, "mInstrumentation");
        Instrumentation mInstrumentation = (Instrumentation) FieldUtils.readField(mInstrumentationField, target);
        if (!PluginInstrumentation.class.isInstance(mInstrumentation)) {
            PluginInstrumentation pit = new PluginInstrumentation(mInstrumentation);
            pit.setEnable(isEnable());
            mPluginInstrumentations.add(pit);
            FieldUtils.writeField(mInstrumentationField, target, pit);
            DLog.i(TAG, "Install Instrumentation Hook old=%s,new=%s", mInstrumentationField, pit);
        } else {
            DLog.i(TAG, "Instrumentation has installed,skip");
        }
    }

    @Override
    public void onUnInstall(ClassLoader classLoader) throws Throwable {

    }
}
