package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;

import com.cuan.helper.log.LogUtil;
import com.cuan.plugincore.plugin.Plugin;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginPackageInfo;
import com.cuan.plugincore.plugin.PluginSignatureInfo;

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
    private ActivityThread activityThread  = null;
    private static PluginManager instance  = null;

    private volatile PluginInstaller installer;

    private PluginManager(){
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
     * TODO: 添加宿主进程启动时从assets中默认插件目录中提取自有插件的逻辑
     * TODO: 该方法应该保证在Application对象中执行,如何保证呢?
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

        /**
         * 初始化installer
         * TODO:单独进程？
         */
        installer =  PluginInstaller.getInstance();
        installer.init(context);

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
     * 安装一个插件
     * @param apkFilePath
     * @param isSelfPlugin
     */
    public void installPlugin(final String apkFilePath,final boolean isSelfPlugin){
        if(installer == null){
            installer = PluginInstaller.getInstance();
            installer.init(hostContext);
        }
        installer.installPlugin(apkFilePath,isSelfPlugin);
    }
    public void asyncInstallPlugin(final String apkFilePath,final boolean isSelfPlugin){
        if(installer == null){
            installer = PluginInstaller.getInstance();
            installer.init(hostContext);
        }
        installer.asyncInstallPlugin(apkFilePath,isSelfPlugin);
    }

    /**
     * 以异步方式卸载一个插件(并没有删除沙箱目录)
     * @param plugin
     */
    public void asyncUnInstallPlugin(final Plugin plugin){
        if(installer == null){
            installer = PluginInstaller.getInstance();
            installer.init(hostContext);
        }
        installer.asyncUnInstallPlugin(plugin);
    }
    /**
     * 从数据库中查询之前安装的插件信息,并解析;
     * 同时将安装文件丢失的(即apk文件丢失)的插件信息
     * 从相关数据库中移除。
     */
    public void loadPlugins(){

        //用于存放异常插件
        ArrayList<PluginInfo> exceptionPlugins = new ArrayList<PluginInfo>(PluginConstants.maxExceptionNums);

        // 从数据库中读取之前安装的插件信息
        Realm realm = Realm.getInstance(hostContext);
        RealmResults<PluginInfo> infos = realm.where(PluginInfo.class).findAll();

        if (infos != null && infos.size() > 0) {
            for (PluginInfo info : infos) {
                File pluginPathFile = new File(info.getPluginPath());
                // 安装文件丢失
                if (!pluginPathFile.exists()) {
                    LogUtil.e("[loadPlugins:] exception bundle: %s",info.getPackageName());
                    exceptionPlugins.add(info);
                    continue;
                }
                // 创建Plugin对象
                Plugin plugin = new Plugin(PluginUtils.makePluginInfoFromRealm(info));
                if(PluginConstants.DEBUG)
                    LogUtil.d("[loadPlugin:] %s",plugin.getPackageName());
                // 加载plugin
                installer.loadPlugin(info.getPackageName(),plugin);
            }
        }

        // 清除掉数据库的残留信息
        for (PluginInfo info : exceptionPlugins) {
            RelamUtil.removePluginInfo(info,Realm.getInstance(hostContext));
        }
        realm.close();
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

    public ActivityThread getActivityThread() {
        if (activityThread == null) {
            activityThread = ActivityThread.currentActivityThread();
        }
        return activityThread;
    }

    /**
     * 以包名获取对于的plugin
     * @param packageName
     * @return
     */
    public Plugin getPluginByPackageName(String packageName) {
        if(installer == null){
            installer = PluginInstaller.getInstance();
            installer.init(hostContext);
        }
        return installer.getPluginByPackageName(packageName);
    }

    /**
     * 仅仅供测试使用
     * @return
     */
    public ActivityThread getCurrentActivityThread(){
        return ActivityThread.currentActivityThread();
    }
}
