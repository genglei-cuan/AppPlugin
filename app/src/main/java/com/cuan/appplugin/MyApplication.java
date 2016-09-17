package com.cuan.appplugin;

import android.app.Application;
import android.content.Context;

import com.cuan.plugincore.pluginmanager.PluginManager;

/**
 * Created by genglei.cuan on 16/9/18.
 * genglei.cuan@godinsec.com
 */

public class MyApplication extends Application {
    private PluginManager pluginManager;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /**
         * 初始化并加载之前安装过的插件
         */
        pluginManager = PluginManager.getInstance();
        pluginManager.init(this);
        pluginManager.loadPlugins();
        pluginManager.asyncInstallPlugin("/data/local/tmp/com.immomo.momo_7.0.1_857.apk",false);
    }
}
