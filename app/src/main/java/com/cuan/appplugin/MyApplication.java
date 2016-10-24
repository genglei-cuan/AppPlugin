package com.cuan.appplugin;


import android.app.Application;
import android.content.Context;

import com.cuan.helper.log.DLog;
import com.cuan.helper.reflect.Reflect;
import com.cuan.plugincore.hook.HookManager;
import com.cuan.plugincore.pluginmanager.PluginManager;
import com.cuan.plugincore.servicemanager.ServiceManager;

/**
 * Created by genglei.cuan on 16/9/18.
 * genglei.cuan@godinsec.com
 */

public class MyApplication extends Application {
    private PluginManager pluginManager;
    private HookManager hookManager;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /**
         * 初始化并加载之前安装过的插件
         */
        pluginManager = PluginManager.getInstance();
        hookManager = HookManager.getInstance();
        pluginManager.init(this);
        hookManager.installAllHook(null);
        hookManager.setAllHookEnable(true);
        pluginManager.loadPlugins();
        pluginManager.installPlugin("/data/local/tmp/testplugin-debug.apk",false);


        DLog.i("PluginInstrumentation","PluginInstrumentation: "+ Reflect.on(Reflect.on("android.app.ActivityThread").call("currentActivityThread").get()).field("mInstrumentation").get());

        //ServiceManager.
    }
}
