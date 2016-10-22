package com.cuan.plugincore.hook.base;

/**
 * Created by genglei.cuan on 2016/10/21.
 * genglei.cuan@godinsec.com
 */

/**
 * 参考 360 DroidPlugin 的 hook 框架
 *
 * 该hook框架主要用来hook Android 系统的 service。
 *
 * Android 系统 service 几乎都采用的 binder框架，而客户端中拿到的是 service 的代理对象。
 * 该代理对象中内聚了一个 service 的 binder 引用对象。
 *
 * 无论是代理对象还是binder引用对象，都要继承一个 该binder service 的协议接口。
 *
 * 对 Android 系统 service 的 hook 可以转换为对一个接口对象的 hook ，可以采用java的动态代理技术来实现。
 * 即替换掉service 的 binder 引用对象 或者 代理对象。
 *
 * hook 系统 service 的代码存放在 binder 文件夹中。
 *
 * 当然该 hook  框架还可以用来 hook 一般的静态类型的接口对象，此种类型的 hook 代码存放在 proxy 文件夹中
 *
 */
public abstract class Hook {


    /**
     * hook是否使能
     */
    private boolean mEnable = true;

    /**
     * 用来处理被hook的接口对象的方法执行过程
     */
    protected BaseHookHandle mHookHandle;

    protected abstract BaseHookHandle createHookHandle();

    public boolean ismEnable() {
        return mEnable;
    }

    public void setmEnable(boolean mEnable) {
        this.mEnable = mEnable;
    }

    /**
     * 生成新的接口对象，替换原来的接口对象
     *
     * @param classLoader
     * 一般与被hook的接口对象的类加载器一致
     */
    public abstract void onInstall(ClassLoader classLoader) throws Throwable;


    /**
     * 恢复原来的接口对象
     *
     * @param classLoader
     * 一般与被hook的接口对象的类加载器一致
     */
    public abstract void onUnInstall(ClassLoader classLoader) throws Throwable;

    public boolean isEnable() {
        return mEnable;
    }

}
