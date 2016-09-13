package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-12.
 */


import android.content.pm.PackageInfo;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * 记录插件信息
 *
 * 1. 包名,版本号,是否是自有插件.资源类型等信息,这些信息需要存储到数据库中
 *
 * 2. 该插件的解析结果PackageInfo,不需要存数据库
 *
 */
public class PluginInfo extends RealmObject {

    @PrimaryKey
    private long id;
    private String PackageName;
    private String pluginPath;
    private String pluginDataDir;
    private String libraryPath;
    private String optimized;
    private int version;
    /**
     * 资源的类型,比如说此资源是为了更新某个so库,可以通过metaData获取
     */
    private String type;
    /**
     * 该资源是自有插件还是第三方app
     */
    private boolean isSelfPlugin;

    @Ignore
    private PackageInfo packageInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelfPlugin() {
        return isSelfPlugin;
    }

    public void setSelfPlugin(boolean selfPlugin) {
        isSelfPlugin = selfPlugin;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getPluginDataDir() {
        return pluginDataDir;
    }

    public void setPluginDataDir(String pluginDataDir) {
        this.pluginDataDir = pluginDataDir;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getOptimized() {
        return optimized;
    }

    public void setOptimized(String optimized) {
        this.optimized = optimized;
    }
}
