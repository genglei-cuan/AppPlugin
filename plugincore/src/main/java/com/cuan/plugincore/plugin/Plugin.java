package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Parcel;

import com.cuan.plugincore.plugin.PluginClassloader;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginModule;
import com.cuan.plugincore.pluginmanager.PluginManager;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 代表一个插件
 */
public class Plugin {


    private PluginInfo pluginInfo;
    private PluginModule pluginModule;
    private PluginClassloader pluginClassloader;

    public Plugin(PluginInfo info){
        this.pluginInfo  = info;
    }

    public PluginModule getPluginModule() {
        if(pluginModule == null){

        }
      return pluginModule;
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    private PackageInfo getPackageInfo(PluginInfo pluginInfo) {
        PackageInfo packageInfo = null;
        return packageInfo;
    }

}
