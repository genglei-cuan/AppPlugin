package com.cuan.plugincore.pluginmanager.process;

import android.os.Binder;
import android.os.IBinder;

/**
 * Created by genglei-cuan on 16-10-24.
 */

/**
 * 记录插件插件进程的相关信息;
 * 每当创建一个插件进程时,都要创建一个该实例,
 * 并且要向PluginManager中传递其代理binder,以便PluginManager查询插件进程信息
 */
public class PluginClient extends Binder implements IPluginClient {

    private static String TAG = "PluginClient";
    /**
     * 进程名字
     */
    private String mProcessName;
    /**
     * 真实进程号
     */
    private int mPid;
    /**
     * 虚拟进程号,由PluginManager分配
     */
    private int mVPid;
    /**
     * 虚拟用户id,由PluginManager分配
     */
    private int mVUid;


    /**
     * 插件进程环境是否初始化完毕:例如是否hook
     */
    private boolean mEnvInit;


    /**
     * 单例
     */
    private static PluginClient mClient = null;

    private PluginClient(){}

    /**
     * 创建Client
     */
    public static void MakeClient(){
        if(mClient == null)
            mClient = new PluginClient();
    }

    /**
     * 获取clinet
     * @return
     */
    public PluginClient getClient(){
        return mClient;
    }

    /**
     * 反向注册
     */
    public void registerToPluginManager(){

    }
    @Override
    public IBinder asBinder() {
        return null;
    }
}
