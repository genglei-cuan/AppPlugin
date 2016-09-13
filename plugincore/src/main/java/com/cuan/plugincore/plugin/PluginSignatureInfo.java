package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-13.
 */

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 序列化签名
 *
 * 单独存储签名的一个原因是早期Android版本中存在一个bug,
 * PackageManager的getPackageArchiveInfo缺少对GET_SIGNATURES处理
 *
 * TODO: 暂时认为APK中的签名只有一个,后续在看情况优化吧
 */
public class PluginSignatureInfo extends RealmObject {
    @PrimaryKey
    private String packageName;
    private byte[] infoData;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public byte[] getInfoData() {
        return infoData;
    }

    public void setInfoData(byte[] infoData) {
        this.infoData = infoData;
    }

}
