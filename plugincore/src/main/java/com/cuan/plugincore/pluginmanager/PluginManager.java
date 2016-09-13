package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;

import com.cuan.helper.log.LogUtil;
import com.cuan.plugincore.plugin.PluginInfo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 管理所有的插件。
 *
 * 1. 初始化插件数据沙箱目录
 *
 * 2. 初始化插件安装目录
 *
 * 3. 初始化自带插件的存储路径
 *
 * 4. 数据库记录按照的package以及版本，便于执行升级逻辑
 */
public class PluginManager {


    private static String hostpath = null;
    private static Context hostContext     = null;
    private static PackageManager hostPm   = null;
    private static ActivityManager hostAms = null;


    private static PluginManager instance = null;

    private PluginInstaller installer;

    private PluginManager(){
        installer = PluginInstaller.getInstance();
    }

    public static PluginManager getInstance(){
        if(instance != null)
            return instance;
        else
            return new PluginManager();
    }

    /**
     * 每个app进程中尽可能早的调用该方法进行初始化
     * @param context
     *
     * TODO: 添加从assets中默认插件目录中提取自有插件的逻辑
     */
    public void init(Context context){

        /**
         * 拿到宿主App的context
         */
        if(hostContext == null)
            hostContext = context;

        /**
         * 拿到系统的pms
         */
        if(hostPm == null)
            hostPm = context.getPackageManager();

        /**
         * 拿到系统的ams
         */
        if(hostAms == null)
            hostAms = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        if(hostpath == null)
            hostpath = context.getFilesDir().getParent();
        LogUtil.i("hostPath: %s",hostpath);
        /**
         * 检查所需的文件夹是否存在，不存在则创建
         */
        File installPath = new File(installer.getPluginInstallsDir());
        if(!installPath.exists())
            installPath.mkdir();

        File dataPath = new File(installer.getPluginDatasDir());
        if(!dataPath.exists())
            dataPath.mkdir();

        //从assets中提取自有插件

    }

    /**
     * 从数据库中查询之前安装的插件信息,并安装
     *
     * 需要在数据库中做好记录:包名,版本号,插件apk路径
     */
    public void loadPlugins(){

    }

    public static PackageManager getHostPm() {
        return hostPm;
    }

    public static ActivityManager getHostAms() {
        return hostAms;
    }

    public Context getHostContext(){
        return  hostContext;
    }

    /**
     * 下面这些服务无需hook,直接使用系统的即可
     * @param serviceName
     * @return
     */
    public static boolean useHostSystemService(String serviceName) {
        if (Context.WIFI_SERVICE.equals(serviceName) || Context.LOCATION_SERVICE.equals(serviceName)
                || Context.TELEPHONY_SERVICE.equals(serviceName)
                || Context.CLIPBOARD_SERVICE.equals(serviceName)
                || Context.INPUT_METHOD_SERVICE.equals(serviceName)) {
            return true;
        }
        return false;
    }
    /**
     * 获取宿主的类加载器；
     * 从逻辑上来说宿主的类加载器理应是插件加载器的父加载器
     * 自定义的Plugin类加载器可能需要使用它加载一些系统和Host平台上的类(针对自有插件可能有此需求)
     *
     * TODO: 后续可能对于加载第三方App的classloader,不希望自己的父加载器是宿主,此处有可能调整
     * @return
     */
    public ClassLoader getParentClassLoader() {
        return getClass().getClassLoader();
    }

}
