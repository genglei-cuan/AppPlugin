package com.cuan.plugincore.plugin;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by genglei-cuan on 16-9-13.
 */

/**
 * 序列化存储APK解析结果
 * TODO: 是否添加对签名的存储,方便获取签名???
 */
public class PluginPackageInfo extends RealmObject {
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
