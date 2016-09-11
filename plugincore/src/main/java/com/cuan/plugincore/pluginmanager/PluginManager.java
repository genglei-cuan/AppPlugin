package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

import helper.log.LogUtil;

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

    private static final String pluginInstallPath = "plugins";
    private static final String pluginDataPath    = "pluginData";
    private static final String defaultPlugins    = "defaultPlugins";
    private static String hostpath = null;


    private static Context hostContext     = null;


    private static PackageManager hostPm   = null;
    private static ActivityManager hostAms = null;


    private static PluginManager instance = null;

    private PluginManager(){};

    public PluginManager getInstance(){
        if(instance != null)
            return instance;
        else
            return new PluginManager();
    }

    /**
     * 每个app进程中尽可能早的调用该方法进行初始化
     * @param context
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
        File installPath = new File(hostpath+File.pathSeparator+pluginDataPath);
        if(!installPath.exists())
            installPath.mkdir();

        File dataPath = new File(hostpath+File.pathSeparator+pluginDataPath);
        if(!dataPath.exists())
            dataPath.mkdir();

        File defaultPath = new File(hostpath+File.pathSeparator+defaultPlugins);
        if(!defaultPath.exists())
            defaultPath.mkdir();

    }

    /**
     * 从默认插件目录中加载插件。
     *
     * 一般宿主App自己所需的插件(根据项目需要拆分的子块)都放到这里。
     *
     * 此处不加载第三方App。
     *
     * 需要在数据库中做好记录:包名+版本号
     */
    public void loadPlugins(){

    }

    /**
     * 安装某一个插件
     *
     * 独立的第三方App都通过此方法加载
     *
     * 需要在数据库中做好记录:包名+版本号
     */
    public void installPlugin(){

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
    public static PackageManager getHostPm() {
        return hostPm;
    }

    public static ActivityManager getHostAms() {
        return hostAms;
    }

}
