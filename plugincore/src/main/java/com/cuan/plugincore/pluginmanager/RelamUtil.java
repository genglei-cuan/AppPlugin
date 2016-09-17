package com.cuan.plugincore.pluginmanager;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import com.cuan.helper.parcel.ParcelableUtils;
import com.cuan.plugincore.plugin.PluginInfo;
import com.cuan.plugincore.plugin.PluginPackageInfo;
import com.cuan.plugincore.plugin.PluginSignatureInfo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by genglei-cuan on 16-9-13.
 */

public class RelamUtil {
    /**
     * 需要手动调用realm.close();
     * @param pluginInfo
     * @param realm
     */
    public static void savePackageInfo(PluginInfo pluginInfo,Realm realm) {
        PluginPackageInfo info = new PluginPackageInfo();
        info.setPackageName(pluginInfo.getPackageName());
        info.setInfoData(ParcelableUtils.marshall(pluginInfo.getPackageInfo()));
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();
    }

    /**
     * 需要手动调用realm.close();
     * @param pluginInfo
     * @param realm
     */
    public static void saveSignatureInfo(PluginInfo pluginInfo,Realm realm) {
        PluginSignatureInfo info = new PluginSignatureInfo();
        info.setPackageName(pluginInfo.getPackageName());
        info.setInfoData(ParcelableUtils.marshall(pluginInfo.getPackageInfo().signatures[0]));
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();
    }

    /**
     * 会同时更新签名和packageInfo
     * @param info
     * @param realm
     */
    public static void saveBundleInfo(PluginInfo info,Realm realm) {
        if (info == null || info.getId() < 0) {
            return;
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(info);
        realm.commitTransaction();
        savePackageInfo(info,realm);
        saveSignatureInfo(info,realm);
        realm.close();
    }
    public static synchronized void removePluginInfo(PluginInfo info,Realm realm) {
        if (info == null) {
            return;
        }
        RealmResults<PluginInfo> results = realm.where(PluginInfo.class).equalTo("packageName", info.getPackageName()).findAll();
        RealmResults<PluginSignatureInfo> signatureInfos = realm.where(PluginSignatureInfo.class).equalTo("packageName",info.getPackageName()).findAll();
        RealmResults<PluginPackageInfo> packageInfos = realm.where(PluginPackageInfo.class).equalTo("packageName", info.getPackageName()).findAll();
        realm.beginTransaction();
        if (results != null && results.size() > 0) {
            results.clear();
        }
        if(signatureInfos != null && signatureInfos.size()>0){
            signatureInfos.clear();
        }
        if (packageInfos != null && packageInfos.size() > 0) {
            packageInfos.clear();
        }
        realm.commitTransaction();
        realm.close();
    }

}
