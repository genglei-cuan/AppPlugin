package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.app.ActivityThread;
import android.app.Application;
import android.app.Instrumentation;
import android.app.LoadedApk;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;

import com.cuan.helper.log.LogUtil;
import com.cuan.helper.reflect.Reflect;
import com.cuan.plugincore.pluginmanager.PluginManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * 记录插件运行时信息
 *
 * TODO: 四大组件运行时信息不全,后续要完善
 * TODO: 安装目录中是否应该保存oat文件?
 */
public class PluginModule {

    /**
     * 宿主的context
     */
    private Context hostContext ;

    /**
     * 插件的运行时数据结构,需要修改或者新建
     */
    private ApplicationInfo appInfo;
    private PackageInfo packageInfo;  // 里面应该存储四大组件的所有信息
    private Application application;
    private ClassLoader classLoader;
    private Resources   resources;
    private AssetManager assetManager;
    private ActivityThread hostActivityThread;
    private LoadedApk    loadedApk;
    // 四大组件的而信息,暂时只有Activity
    private Map<String, ActivityInfo> activityInfoMap = new HashMap<String, ActivityInfo>();
    private int themeResId;
    private Resources.Theme theme;

    /**
     * 插件的静态信息
     */
    private String      packageName;
    private String      pluginPath;     // 安装目录
    private File        dataDir;        // 沙箱目录,常用所以为File

    public PluginModule(Context context,
                        String pluginPath,
                        File pluginDataDir,
                        ClassLoader pluginClassloader,
                        PackageInfo packageInfo){
        this.hostContext = context;
        this.pluginPath  = pluginPath;
        this.dataDir     = pluginDataDir;
        this.classLoader = pluginClassloader;
        this.packageInfo = packageInfo;
        hostActivityThread = PluginManager.getInstance().getActivityThread();
        /**
         * 创建资源管理器
         */
        createResources(pluginPath);
        /**
         * 插件初始化
         */
        moduleInit();
    }

    private void moduleInit() {
        packageName = packageInfo.packageName;
        appInfo = parseApplicationInfo(hostContext, packageInfo, 	pluginPath, dataDir.getAbsolutePath());
        themeResId = appInfo.theme;
        /**
         * 创建LoadedApk对象,并修改其mClassLoader
         */
        loadedApk = hostActivityThread.getPackageInfoNoCheck(appInfo,resources.getCompatibilityInfo());
        Reflect.on(loadedApk).set("mClassLoader",classLoader);

        /**
         * 创建plugin的Application对象,并执行其声明周期方法
         */
        long time = System.currentTimeMillis();
        makeApplication();
        LogUtil.i("[makeApplication] " + packageName + " used Time: " + (System.currentTimeMillis() - time));
        if (packageInfo.activities != null) {
            int length = packageInfo.activities.length;
            for (int i = 0; i < length; i++) {
                ActivityInfo info = packageInfo.activities[i];
                activityInfoMap.put(info.name, info);
            }
        }
    }


    /**
     * 创建资源管理器
     * @param path
     */
    private void createResources(String path) {

        AssetManager asset = new AssetManager();
        assetManager = asset;
        asset.addAssetPath(path);
        Resources res = hostContext.getResources();
        resources = new Resources(assetManager, res.getDisplayMetrics(),
                res.getConfiguration());

    }
    /**
     * 修正插件的ApplicationInfo结构
     * 1. apk路径
     * 2. 资源路径
     * 3. 沙箱目录
     * 4. so库路径
     * 5. uid
     * TODO: primaryCpuAbi ? 这些与运行密切相关,可能影响兼容性
     */
    private ApplicationInfo parseApplicationInfo(Context hostContext,
                                                 PackageInfo packageInfo,
                                                 String apkPath,
                                                 String dataDirPath) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (applicationInfo != null) {
            /**
             * 插件共享宿主的uid
             */
            applicationInfo.uid = hostContext.getApplicationInfo().uid;
            applicationInfo.sourceDir = apkPath;
            applicationInfo.dataDir = dataDirPath;
            /**
             * so库暂时放到沙箱目录中去
             * TODO:是否有必要仿照系统那样将其放到安装目录中去? 这样还要做软连接,有必要仿照吗?
             */
            applicationInfo.nativeLibraryDir = dataDir.getAbsolutePath();
            /**
             * TODO: primaryCpuAbi ? 这与运行密切相关,可能影响兼容性
             */
        }
        return applicationInfo;
    }

    /**
     * 为plugin创建Application对象
     *
     * 1. 创建上下文context
     * 2. 创建Application对象
     * 3. 其attachBaseContext和onCreate方法,这里要注意只有当插件中自定义Application时,才调用其attachBaseContext方法
     *
     */
    private void makeApplication(){

        boolean hasApplication = true;

        Instrumentation instrumentation = hostActivityThread.getInstrumentation();

        /**
         * 获取插件的Application class名字;
         * 如果插件中没有自定义Application的话,为null
         */
        String appClassName = appInfo.className;
        if (appClassName == null) {
            appClassName = "android.app.Application";
            hasApplication = false;
        }
        /**
         * 创建上下文context
         *
         * Android 4.4.3之后的接口是createAppContext,之前是先创建context,然后调用init()
         */
        Context pluginContext = null;
        Class<?> cls = null;
        // Android 4.4.3及其之后的版本
        if(Build.VERSION.SDK_INT >= 21){
            pluginContext = Reflect.on("android.app.ContextImpl").call("createAppContext",hostActivityThread,loadedApk).get();
        }else{
            try {
                cls = Class.forName("android.app.ContextImpl");
                Method method = cls.getDeclaredMethod("createAppContext",ActivityThread.class,LoadedApk.class);
                method.setAccessible(true);
                pluginContext = (Context) method.invoke(null,hostActivityThread,loadedApk);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //Android 4.4.2及其之前的版本
                try{
                    if(cls != null){
                        Constructor<?> constructor = cls.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        pluginContext = (Context)constructor.newInstance();
                        Method method = cls.getDeclaredMethod("init",ActivityThread.class, IBinder.class,LoadedApk.class);
                        method.setAccessible(true);
                        method.invoke(pluginContext,loadedApk,null,hasApplication);
                    }
                }catch (Exception ee){

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * 修正ContextImpl.mOuterContext
         */
        if(pluginContext != null){
            Reflect.on("android.app.ContextImpl").create(pluginContext).set("mOuterContext",application);
        }
        PluginContextWrapper pluginContextWrapper = new PluginContextWrapper(pluginContext);
        /**
         * 创建Application对象,并将其更新到LoadedApk.mApplication中
         */
        try {
           application =  instrumentation.newApplication(classLoader,appClassName,pluginContextWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Reflect.on(loadedApk).set("mApplication",application);

        /**
         * 执行attachBaseContext()和onCreate()方法
         */
        if(application != null) {
            if (hasApplication) {
                try {
                    Class<?> cl = Class.forName(appClassName);
                    Method method = cl.getDeclaredMethod("attachBaseContext", Context.class);
                    method.invoke(application, pluginContextWrapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            application.onCreate();
        }

    }
    /**
     * 获取Activity的theme id，如无设置，则返回Application的theme id
     * @return
     */
    public int getThemeResId(String activityClassName) {
        ActivityInfo info = getActivityInfo(activityClassName);
        if (info != null) {
            return info.getThemeResource();
        }
        return themeResId;
    }
    /**
     * 获取Activity的加载模式
     */
    public int getActivityLaunchMode(String activityClassName) {
        ActivityInfo info = getActivityInfo(activityClassName);
        if (info != null) {
            return info.launchMode;
        }
        return ActivityInfo.LAUNCH_MULTIPLE;
    }

    public Resources.Theme getTheme() {
        if (theme == null) {
            theme = resources.newTheme();
        }
        return theme;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Resources getResources() {
        return resources;
    }

    public Application getPulginApplication() {
        return application;
    }

    public int getAppThemeResId() {
        return themeResId;
    }
    public ActivityInfo getActivityInfo(String className) {
        return activityInfoMap.get(className);
    }
    public String getPackageName() {
        return packageName;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public ApplicationInfo getApplicationInfo() {
        return appInfo;
    }

}
