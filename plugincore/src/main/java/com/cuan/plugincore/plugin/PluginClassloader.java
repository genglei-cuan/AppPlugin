package com.cuan.plugincore.plugin;

import com.cuan.helper.log.LogUtil;

import dalvik.system.DexClassLoader;

/**
 * Created by genglei-cuan on 16-9-12.
 */

/**
 * 加载插件的Classloader:加载类和so库
 *
 * TODO: 对于自有的插件当自身找到不某类的时候,可以允许其从宿主和系统加载器中查找,这样可以减少自有插件的大小,但是这样的自有插件是无法独立安装运行;
 *        对于第三方App来说,按找正常逻辑不应该允许其从宿主中查找类,
 *        后续应该做好这方面的权衡,现阶段当插件中找不到某类的时候,统统允许从宿主加载器中查找
 */
public class PluginClassloader extends DexClassLoader {

    private ClassLoader hostClassLoader; // 宿主APK的类加载器, 是Android系统为其生成的PathClassLoader

    public PluginClassloader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        hostClassLoader = parent;
    }
    @Override
    protected Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(className);
        ClassLoader systemClassLoader = hostClassLoader.getParent();
        if (clazz == null) {
            /**
             * java开头或android系统的类从parent的类加载器查找
             */
            if (className.startsWith("android.") || className.startsWith("java.")
                    || className.startsWith("javax.")) {
                try {
                    clazz = systemClassLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                }
            }
            /**
             * 其他自己实现的类从自身查找
             */
            if (clazz == null) {
                try {
                    clazz = findClass(className);
                } catch (Exception e) {
                }
            }
            /**
             * 可以从宿主和系统加载器中查找
             *
             * TODO: 对于自有的插件当自身找到不某类的时候,可以允许其从宿主中查找,这样可以减少自有插件的大小,但是这样的自有插件是无法独立安装运行;
             *        对于第三方App来说,按找正常逻辑不应该允许其从宿主中查找类,
             *        后续应该做好这方面的权衡,现阶段当插件中找不到某类的时候,统统允许从宿主加载器中查找
             */
            if (clazz == null) {
                try {
                    clazz = hostClassLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    clazz = systemClassLoader.loadClass(className);
                }
            }
        }
        if (clazz == null) {
            LogUtil.e("PluginClassLoader Can't find class: " + className);
        }
        return clazz;
    }

    @Override
    public String findLibrary(String name) {
        return super.findLibrary(name);
    }
}
