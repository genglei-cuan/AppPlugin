package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.cuan.helper.log.LogUtil;
import com.cuan.plugincore.plugin.Plugin;
import com.cuan.plugincore.plugin.PluginInfo;



import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * 插件安装器,提供插件的安装,更新和卸载策略。
 */
public class PluginInstaller {


    private Context hostContext;

    private static PluginInstaller instance;

    /**
     * 存储已经安装的插件信息
     */
    private volatile Map<String, Plugin> installedPlugins = new HashMap<String, Plugin>();

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
     * 仅仅供从数据库装载时使用
     * @param packageName
     * @param plugin
     */
    public void loadPlugin(String packageName,Plugin plugin){
        installedPlugins.put(packageName,plugin);
    }
    /**
     * 所有插件apk的根目录:
     *
     * 宿主沙箱目录/plugins
     */
    public String getPluginInstallsDir() {
        return hostContext.getFilesDir().getParent()+File.separator+PluginConstants.pluginInstallPath;
    }

    /**
     * @param pluginPackageName 插件包名
     *
     * @return 特定插件的apk根目录
     */
    public String getPluginInstallDir(@NonNull String pluginPackageName) {
        return getPluginInstallsDir() + File.separator + pluginPackageName;
    }
    /**
     * 所有插件沙箱存放的根目录:
     *
     * 宿主沙箱目录/pluginData
     */
    public String getPluginDatasDir() {
        return hostContext.getFilesDir().getParent()+File.separator+PluginConstants.pluginDataPath;
    }

    /**
     * @param pluginPackageName 插件包名
     *
     * @return 特定插件的沙箱根目录
     */
    public String getPluginDataDir(@NonNull String pluginPackageName) {
        return getPluginDatasDir() + File.separator + pluginPackageName;
    }

    /**
     * 分析apk文件,构建PluginInfo
     *
     * @param pluginPath
     * @param isSelfPlugin 是否是自有插件
     * @return
     */
    public PluginInfo getPluginInfo(String pluginPath,boolean isSelfPlugin) {
        PluginInfo info = null;
        PackageInfo packageInfo = PluginUtils.parseApk(hostContext, pluginPath);
        if (packageInfo != null) {
            if (packageInfo.signatures == null) {
                Signature signatures[] = PluginUtils.collectCertificates(pluginPath,false);
                packageInfo.signatures = signatures;
                /**
                 * TODO:添加自有插件的签名校验,另外校验成功在这里将签名写入数据库?
                 */
                /*
                String signature = PluginConstants.SIGNATURE_PLUGIN;
                if (!isSignaturesSame(signature,  signatures[0])){

                }*/
            }
            info = new PluginInfo();
            info.setPackageName(packageInfo.packageName);
            info.setVersion(packageInfo.versionCode);
            info.setIsSelfPlugin(isSelfPlugin);
            android.os.Bundle metaData = packageInfo.applicationInfo.metaData;
            if (isSelfPlugin && metaData != null) {
                /**
                 * 解析出自有插件的配置信息
                 * TODO: 自有插件配置信息,用来处理自有插件种类?so库升级,资源升级,普通代码升级???
                 */
                info.setType((String) metaData.get("XXX"));
            }
            info.setPackageInfo(packageInfo);
        }
        return info;
    }

    /**
     * 安装插件
     * 1. 解析apk
     * 2. 处理覆盖安装情况:升级与降级
     * 3. 创建安装目录和沙箱目录
     * 4. 拷贝apk到安装目录并解压so库到沙箱目录
     * 5. 保存pluginInfo到数据库
     * 6. 保存packageInfo到数据库
     * 7. 创建插件的classloader,目的是为了执行dex2oat,提高下次再创建classloader的效率
     *
     * @param apkFilePath 插件apk路径
     */
    public boolean installPlugin(final String apkFilePath,final boolean isSelfPlugin){
        /**
         * 获取PluginInfo,此时已经解析过该插件了
         */
        PluginInfo pluginInfo = getPluginInfo(apkFilePath,isSelfPlugin);
        if(null == pluginInfo)
            return false;
        /**
         * 检查是否已经安装
         */
        String packageName = pluginInfo.getPackageName();
        Plugin plugin = installedPlugins.get(packageName);
        if(null != plugin){
            //已经安装的插件版本更高,那就不处理,即不支持降级安装
            if(plugin.getPluginInfo().getVersion()>pluginInfo.getVersion())
                //TODO: 如何向宿主App提示呢?通过Handler?
                return true;
            else{
                //TODO: 升级安装
                return true;
            }
        }

        long pluginId = generatePluginId();
        if (pluginId == -1) {
            LogUtil.e("generated plugin id failed!!!");
            return false;
        }

        pluginInfo.setId(pluginId);
        pluginInfo.setPluginPath(getPluginDataDir(pluginInfo.getPackageName()));
        /**
         * 创建插件安装目录:
         * 宿主沙箱/plugins/package/version版本号/base-1.apk
         * 为了应对应对当插件更新失败时的回滚操作,这里在保存Plugin安装路径中
         * 创建一个"version版本号"的文件夹存储该版本的安装文件;
         * 当插件成功更新后,删除旧版本即可.
         */

        String pluginInstallDir = getPluginInstallDir(pluginInfo.getPackageName())+File.separator+
                                  PluginConstants.VERSION+pluginInfo.getVersion();
        File installDir = new File(pluginInstallDir);
        if(!installDir.exists())
            installDir.mkdirs();
        String pluginInstallPath = pluginInstallDir+File.separator+PluginConstants.FILE_PLUGIN_NAME;
        pluginInfo.setPluginPath(pluginInstallPath);
        /**
         * 创建插件沙箱目录,包括lib目录,dex目录
         *  宿主沙箱/pluginData/package
         */
        String pluginDataDir = getPluginDataDir(pluginInfo.getPackageName());
        File dataDir = new File(pluginDataDir);
        if(!dataDir.exists())
            dataDir.mkdir();
        pluginInfo.setPluginDataDir(pluginDataDir);

        String libraryPath = pluginDataDir +File.separator + PluginConstants.DIR_NATIVE_LIB;
        File libDir = new File(libraryPath);
        if(!libDir.exists())
            libDir.mkdir();
        pluginInfo.setLibraryPath(libraryPath);

        String optimized = pluginDataDir + File.separator + PluginConstants.DIR_DALVIK_CACHE;
        File dexDir = new File(optimized);
        if(!dexDir.exists())
            dexDir.mkdir();
        pluginInfo.setOptimized(optimized);
        /**
         * 拷贝apk到安装目录,并且解压so库
         */
        try {
            PluginUtils.copyAndExtractApkFile(pluginInfo,apkFilePath);
        } catch (Exception e) {
            LogUtil.e("[" + packageName + "] copyAndExtractApkFile error:" + e.getMessage());
        }
        Plugin newPlugin = new Plugin(pluginInfo);
        installedPlugins.put(pluginInfo.getPackageName(), newPlugin);
        /**
         * 将pluginInfo保存到数据库中的同时也会将签名和packageInfo保存到数据库中
         */
        RelamUtil.saveBundleInfo(pluginInfo,Realm.getInstance(hostContext));
        /**
         * 是一个耗时的操作
         */
        newPlugin.getPluginClassloader();
        return true;
    }

    /**
     * 异步安装一个插件
     * @param apkPath
     * @param isSelfPlugin
     * TODO: 是否要为installer创建一个looper消息处理循环呢?
     */
    public void asyncInstallPlugin(final String apkPath,final boolean isSelfPlugin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                installPlugin(apkPath,isSelfPlugin);
            }
        }).start();
    }


    /**
     * 卸载某一个第三方插件
     *
     * 需要从数据库中删除相关信息
     */
    public void asyncUnInstallPlugin(final Plugin plugin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String packageName = plugin.getPackageName();
                if (TextUtils.isEmpty(packageName)) {
                    return;
                }
                plugin.releasePluginModule(); //释放PluginModule
                /**
                 * 删除安装目录,并从数据库中移除相关信息
                 * TODO: 插件沙箱目录需要删除吗??
                 */
                String installPath = plugin.getPluginInfo().getPluginPath();
                File pluginApk = new File(installPath);
                if(!pluginApk.isDirectory()&&pluginApk.exists())
                    pluginApk.delete();
                RelamUtil.removePluginInfo(plugin.getPluginInfo(),Realm.getInstance(hostContext));
                installedPlugins.remove(packageName);
            }
        }).start();
    }

    /**
     * 升级某一个插件，包括宿主App的插件和独立第三方插件
     */
    public  void updatePlugin(){

    }

    /**
     * 检验自有插件的合法性,即校验签名是否与宿主app一致.
     *
     * 第三方app安装时不做签名校验,升级覆盖安装的时候需要.
     *
     * @param pluginPath
     * @return
     *
     * TODO: 后续添加签名校验机制
     */
    public boolean checkPluginValid(String pluginPath){
        return true;
    }



    /**
     * Realm数据库主见不能自增,这里同步产生一个id;
     * 考虑到后续会频繁使用到PluginInfo,所以使用id能快速索引
     * @return
     */
    private synchronized long generatePluginId() {
        long id = -1;
        Realm realm = Realm.getInstance(hostContext);
        long count = realm.where(PluginInfo.class).count();
        if (count > 0) {
            id = realm.where(PluginInfo.class).max("id").intValue() + 1;
        } else {
            id = 0;
        }
        if (id >= 0) {
            PluginInfo info = new PluginInfo();
            info.setId(id);
            realm.beginTransaction();
            realm.copyToRealm(info);
            realm.commitTransaction();
        }
        realm.close();
        return id;
    }
}
