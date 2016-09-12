package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei-cuan on 16-9-12.
 */

/**
 * 插件安装器,提供插件的安装和卸载策略。
 */
public class PluginInstaller {

    /**
     * 安装某一个插件
     *
     * 独立的第三方App都通过此方法加载
     *
     * 需要在数据库中做好记录:包名+版本号
     */
    public void installPlugin(){

    }

    /**
     * 卸载某一个第三方插件
     *
     * 需要从数据库中删除相关信息
     */
    public void uninstallPlugin(){

    }

    /**
     * 升级某一个插件，包括宿主App的插件和独立第三方插件
     */
    public  void updatePlugin(){

    }
}
