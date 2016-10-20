package com.cuan.plugincore.plugin;

import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

/**
 * Created by genglei.cuan on 2016/10/20.
 * genglei.cuan@godinsec.com
 */

/**
 * 因为Android系统默认是使用PathClassLoader加载apk的，为了最大化还原真实环境，
 * 做了一个内聚PluginClassLoader的傀儡classloader.
 */
public class PluginClassLoaderDelegate extends PathClassLoader {

    private PluginClassloader pluginLoader;

    public PluginClassLoaderDelegate(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super("", parent);
        pluginLoader = new PluginClassloader(dexPath,optimizedDirectory,librarySearchPath,parent);

    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return pluginLoader.loadClass(name,resolve);
    }

    @Override
    public String findLibrary(String name) {
        return pluginLoader.findLibrary(name);
    }

    @Override
    protected URL findResource(String name) {
        return pluginLoader.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) {
        return pluginLoader.findResources(name);
    }

    @Override
    public String toString() {
        return pluginLoader.toString();
    }
}
