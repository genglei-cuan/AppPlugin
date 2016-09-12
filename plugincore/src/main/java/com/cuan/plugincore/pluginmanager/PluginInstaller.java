package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;

import com.cuan.plugincore.plugin.PluginClassloader;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginModule;


import java.io.File;

/**
 * 插件安装器,提供插件的安装,更新和卸载策略。
 */
public class PluginInstaller {


    private Context hostContext;

    private static PluginInstaller instance;

    public static PluginInstaller getInstance() {
        if (instance == null) {
            instance = new PluginInstaller();
        }
        return instance;
    }


    private PluginInstaller(){}

    public void init(Context context){
        this.hostContext = context;
    }
    /**
     * 所有插件沙箱存放的根目录:
     *
     * 宿主沙箱目录/app_plugin
     */
    public String getPluginRootDir() {
        return hostContext.getDir(PluginConstants.DIR_PLUGIN, Context.MODE_PRIVATE).getAbsolutePath();
    }

    /**
     * @param pluginId 插件包名
     *
     * @return 特定插件的沙箱根目录
     */
    public String getPluginDir(@NonNull String pluginId) {
        return getPluginRootDir() + File.separator + pluginId;
    }
    /**
     *
     * @param pluginId 插件包名
     * @param pluginVersion 插件版本号
     * @return true:已经安装;false:没有安装
     *
     * TODO:当插件在系统中安装了呢? 例如lbe平行空间对系统中已经安装的app的双开的实现。
     */
    public boolean isPluginInstalled(String pluginId, String pluginVersion) {
        if (PluginConstants.ignoreInstalledPlugin) {
            // 后续添加其他逻辑
           return false;
        }
        return checkPluginValid(pluginId, pluginVersion, true);
    }

    public boolean isPluginInstalled(String pluginPath) {
        if (PluginConstants.ignoreInstalledPlugin) {
            // 强制使用外部插件
            return false;
        }
        PackageInfo packageInfo = getPluginInfo(pluginPath);
        return packageInfo != null && checkPluginValid(packageInfo.packageName, String.valueOf(packageInfo.versionCode), true);
    }

    public PackageInfo getPluginInfo(String pluginPath) {
        return ApkUtil.getPackageInfo(context, pluginPath);
    }
    /**
     * 安装某一个插件
     *
     * 对自有插件的安装和升级时需要校验其和宿主app的签名是否一致
     *
     * 对于第三方App安装时不做签名校验,升级时需要
     *
     * 需要在数据库中做好记录:包名+版本号
     */
    public PluginModule installPlugin(PluginInfo pluginInfo){
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
        PluginClassloader pluginClassloader = new PluginClassloader(apkPath, optimized, libraryPath, parent);
        PackageInfo packageInfo = pluginInfo.getPackageInfo();
        Context hostContext = PluginManager.getInstance().getHostContext();
        if (packageInfo == null) {
            packageInfo = getPackageInfo(pluginInfo);
            pluginInfo.setPackageInfo(packageInfo);
        }

        PluginModule pluginModule = new PluginModule(hostContext, apkPath, pluginDataFile, pluginClassloader, packageInfo);
        return pluginModule;
    }

    /**
     * 卸载某一个第三方插件
     *
     * 需要从数据库中删除相关信息
     */
    public void uninstallPlugin(){

    }

    /**
     * 升级某一个插件，包括宿主App的插件和独立第三方插件
     */
    public  void updatePlugin(){

    }
}
