package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Parcel;
import android.text.TextUtils;

import com.cuan.helper.log.LogUtil;
import com.cuan.helper.parcel.ParcelableUtils;
import com.cuan.plugincore.plugin.PluginClassloader;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginModule;
import com.cuan.plugincore.pluginmanager.PluginManager;
import com.cuan.plugincore.pluginmanager.PluginUtils;
import com.cuan.plugincore.pluginmanager.RelamUtil;

import java.io.File;

import dalvik.system.PathClassLoader;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 代表一个插件
 *
 * 一个插件中的信息可以分为两大类:
 * 1. 静态信息:PluginInfo
 * 2. 动态信息:PluginModule,也可理解为运行时信息
 */
public class Plugin {


    private String packageName;
    private String type;// 自有插件的类型
    private PluginInfo pluginInfo;
    private PluginModule pluginModule;//大型数据结构,不使用时要及时释放
    private PluginClassLoaderDelegate pluginClassloader;

    /**
     * 以静态信息PluginInfo来创建一个Plugin
     * @param info
     */
    public Plugin(PluginInfo info){
        this.pluginInfo  = info;
        type = info.getType();
        packageName = info.getPackageName();
    }

    public PluginModule getPluginModule() {
        /**
         * 第一次获取时创建
         */
        if(pluginModule == null){
            ClassLoader parent = PluginManager.getInstance().getParentClassLoader();
            PluginClassloader pluginClassloader = new PluginClassloader(pluginInfo.getPluginPath(),
                    pluginInfo.getOptimized(),
                    pluginInfo.getLibraryPath(),parent);
            PackageInfo packageInfo = pluginInfo.getPackageInfo();
            Context hostContext = PluginManager.getInstance().getHostContext();
            /**
             * 这里为null时,说明非首次启动,需要反序列化得到packageInfo
             */
            if (packageInfo == null) {
                packageInfo = getPackageInfo(pluginInfo);
                pluginInfo.setPackageInfo(packageInfo);
            }
            File pluginDataFile = new File(pluginInfo.getPluginDataDir());
            pluginModule = new PluginModule(hostContext, pluginInfo.getPluginPath(), pluginDataFile, pluginClassloader, packageInfo);
        }
      return pluginModule;
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    /**
     * 从数据库中获取PackageInfo信息
     * 如果在数据库中未保存有PackageInfo那么从APK文件解析,此种情况正常时,应该不会发生
     */
    private PackageInfo getPackageInfo(PluginInfo pluginInfo) {
        PackageInfo packageInfo = null;
        Context hostContext = PluginManager.getInstance().getHostContext();
        Realm realm = Realm.getInstance(hostContext);
        RealmQuery<PluginPackageInfo> query = realm.where(PluginPackageInfo.class);
        PluginPackageInfo info = query.equalTo("packageName", pluginInfo.getPackageName()).findFirst();
        //从数据库获取的数据转换成PackageInfo
        if (info != null) {
            Parcel parcel = ParcelableUtils.unmarshall(info.getInfoData());
            try {
                packageInfo = PackageInfo.CREATOR.createFromParcel(parcel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 从APK文件解析出PackageInfo，并存入数据库中
        if (packageInfo == null) {
            packageInfo =  PluginUtils.parseApk(hostContext, pluginInfo.getPluginPath());
            if (packageInfo.signatures == null) {
                Signature signatures[] = PluginUtils.collectCertificates(pluginInfo.getPluginPath(),false);
                packageInfo.signatures = signatures;
                /**
                 * TODO:添加自有插件的签名校验
                 */
                /*
                String signature = PluginConstants.SIGNATURE_PLUGIN;
                if (!isSignaturesSame(signature,  signatures[0])){

                }*/
                RelamUtil.saveSignatureInfo(pluginInfo, realm);
            }
            RelamUtil.savePackageInfo(pluginInfo,realm);
            realm.close();
        }

        realm.close();
        return packageInfo;
    }

    public PathClassLoader getPluginClassloader() {
        if(null == pluginClassloader){
            String apkPath = pluginInfo.getPluginPath();
            String libraryPath = pluginInfo.getLibraryPath();
            String optimized = pluginInfo.getOptimized();
            /**
             * 宿主的classloader
             */
            ClassLoader parent = PluginManager.getInstance().getParentClassLoader();
            /**
             * 首次会执行dex2oat操作,是一个耗时操作,当产生了oat文件后,下一次会快很多
             */
            pluginClassloader = new PluginClassLoaderDelegate(apkPath, optimized, libraryPath, parent);
        }
        return pluginClassloader;
    }

    public void setPluginClassloader(PluginClassLoaderDelegate pluginClassloader) {
        this.pluginClassloader = pluginClassloader;
    }

    private static boolean isSignaturesSame(String s1, Signature s2) {
        if (TextUtils.isEmpty(s1))
            return false;
        if (s2 == null)
            return false;
        String item = s2.toCharsString().toLowerCase();
        if (item.equalsIgnoreCase(s1))
            return true;
        return false;
    }
    public void releasePluginModule() {
        if (pluginModule != null) {
            pluginModule = null;
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public String getType() {
        return type;
    }


}
