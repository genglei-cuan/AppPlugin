package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-12.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Parcel;

import com.cuan.helper.parcel.ParcelableUtils;
import com.cuan.plugincore.plugin.PluginClassloader;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginModule;
import com.cuan.plugincore.pluginmanager.PluginManager;
import com.cuan.plugincore.pluginmanager.PluginUtils;
import com.cuan.plugincore.pluginmanager.RelamUtil;

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
    public void setPluginModule(PluginModule pluginModule) {
        this.pluginModule = pluginModule;
    }


    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    /**
     * 获取PackageInfo信息
     * 如果在数据库中未保存有PackageInfo (Parcelable对像)，则从APK文件解析，并转换为byte[]保存入数据库。
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
                RelamUtil.saveSignatureInfo(pluginInfo, signatures[0], realm);
            }
            RelamUtil.savePackageInfo(pluginInfo, packageInfo, realm);
        }

        realm.close();
        return packageInfo;
    }

    public PluginClassloader getPluginClassloader() {
        return pluginClassloader;
    }

    public void setPluginClassloader(PluginClassloader pluginClassloader) {
        this.pluginClassloader = pluginClassloader;
    }
}
