package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/13.
 * genglei.cuan@godinsec.com
 */

/**
 * 插件框架中用到的常量
 */
public class PluginConstants {

    /**
     * 调试开关
     */
    public static boolean DEBUG = false;

    /**
     * 是否忽略内部安装的插件,而使用外部系统中安装的插件(当独立插件在系统中正常安装后)
     */
    public static boolean ignoreInstalledPlugin = false;

    /**
     * 插件安装目录和沙箱目录的文件夹常量
     */
    public static final String DIR_TEMP_SO = "temp";
    public static final String DIR_DALVIK_CACHE = "dalvik-cache";
    public static final String DIR_NATIVE_LIB = "lib";
    public static final String DIR_PLUGIN = "plugin";
    public static final String FILE_PLUGIN_NAME = "base-1.apk";
    public static final String EXT_TEMP_PLUGIN = ".apk";
    public static final String VERSION = "version";

    /**
     * 验证自有插件签名用的公钥
     */
    public static final String SIGNATURE_PLUGIN = "XXXXXXX";

    public static void setDebugMode(boolean isDebuggable, boolean ignoreInstalledPlugin) {
        PluginConstants.DEBUG = isDebuggable;
        PluginConstants.ignoreInstalledPlugin = ignoreInstalledPlugin;
    }
}
