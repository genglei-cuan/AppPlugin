package com.cuan.plugincore.hook;

/**
 * Created by genglei.cuan on 2016/10/22.
 * genglei.cuan@godinsec.com
 */

import android.content.Context;

import com.cuan.helper.log.DLog;
import com.cuan.plugincore.hook.base.Hook;
import com.cuan.plugincore.hook.handle.PluginInstrumentation;
import com.cuan.plugincore.hook.proxy.InstrumentationHook;
import com.cuan.plugincore.hook.proxy.PluginCallbackHook;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理hook的安装、卸载与使能
 */
public class HookManager {
    private static String TAG = "HookManager";

    private static HookManager sInstance = null;

    private List<Hook> mHookList = new ArrayList<>(5);

    private HookManager(){};


    public void setHookEnable(Class hookclass,boolean enable){
        for(Hook hook : mHookList){
            if(hookclass.isInstance(hook))
                hook.setEnable(enable);
        }
    }

    public void setAllHookEnable(boolean enable){
        for(Hook hook : mHookList){
            hook.setEnable(enable);
        }
    }

    public static HookManager getInstance(){
        if(sInstance == null)
            sInstance = new HookManager();
        return sInstance;
    }

    public void installHook(Hook hook,ClassLoader classLoader){
        try{
            hook.onInstall(classLoader);
            synchronized (mHookList){
                mHookList.add(hook);
            }
        }catch (Throwable e){
            DLog.e(TAG, "installHook %s error", e, hook);
        }
    }
    public void installAllHook(ClassLoader classLoader){

        installHook(new InstrumentationHook(),classLoader);
        installHook(new PluginCallbackHook(),classLoader);

    }

}
