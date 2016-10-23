package com.cuan.plugincore.hook.handle;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.cuan.helper.log.DLog;

/**
 * Created by genglei.cuan on 2016/10/23.
 * genglei.cuan@godinsec.com
 */

/**
 * 控制插件Activity生命周期的回调
 */
public class PluginInstrumentation extends Instrumentation {

    private static String TAG = "PluginInstrumentation";

    private Instrumentation mBase;

    private boolean mEable =  false;


    public PluginInstrumentation(Instrumentation mBase){
        this.mBase = mBase;
    }

    public void setEnable(boolean mEable){
        this.mEable = mEable;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        DLog.i(TAG,"---newActivity---");
        return super.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        DLog.i(TAG,"---callActivityOnCreate---");
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        DLog.i(TAG,"---callActivityOnDestroy---");
        super.callActivityOnDestroy(activity);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        DLog.i(TAG,"---callApplicationOnCreate---");
        super.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        DLog.i(TAG,"---callActivityOnStart---");
        super.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        DLog.i(TAG,"---callActivityOnRestart---");
        super.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        DLog.i(TAG,"---callActivityOnNewIntent---");
        super.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        DLog.i(TAG,"---callActivityOnResume---");
        super.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        DLog.i(TAG,"---callActivityOnStop---");
        super.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        DLog.i(TAG,"---callActivityOnPause---");
        super.callActivityOnPause(activity);
    }
}
