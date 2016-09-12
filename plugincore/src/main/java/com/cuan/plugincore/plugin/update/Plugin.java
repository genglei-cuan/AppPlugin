package com.cuan.plugincore.plugin.update;

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
 *
 * TODO: 拆分安装逻辑到PluginInstaller中
 */
public class Plugin {

    private static final String VERSION = "version";
    private static final String ODEX = "odex";
    private static final String DATA = "data";
    private PluginInfo pluginInfo;
    private PluginModule pluginModule;
    private PluginClassloader pluginClassloader;

    public Plugin(PluginInfo info){
        this.pluginInfo  = info;
    }

    public PluginModule getPluginModule() {
        if(pluginModule == null){
            String apkPath = pluginInfo.getPluginPath();
            /**
             * 为了应对应对当插件更新失败时的回滚操作,这里在保存Plugin安装路径中
             * 创建一个"version版本号"的文件夹存储该版本的安装文件;
             * 当插件成功更新后,删除旧版本即可.
             *
             * 创建安装目录,odex目录和沙箱目录
             */
            String libraryPath = pluginInfo.getPluginPath() + File.pathSeparator + "VERSION" + pluginInfo.getVersion() + "/lib";
            String optimized = pluginInfo.getPluginPath() + File.pathSeparator + "VERSION" + pluginInfo.getVersion() + File.pathSeparator + ODEX;
            File pluginDataFile = new File(pluginInfo.getPluginPath() + File.pathSeparator + DATA);
            ClassLoader parent = PluginManager.getInstance().getParentClassLoader();
            if ( pluginClassloader == null) {
                pluginClassloader = new PluginClassloader(apkPath, optimized, libraryPath, parent);
            }
            PackageInfo packageInfo = pluginInfo.getPackageInfo();
            Context hostContext = PluginManager.getInstance().getHostContext();
            if (packageInfo == null) {
                packageInfo = getPackageInfo(pluginInfo);
                pluginInfo.setPackageInfo(packageInfo);
            }
            pluginModule= new PluginModule(hostContext, apkPath, pluginDataFile, pluginClassloader, packageInfo);
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
