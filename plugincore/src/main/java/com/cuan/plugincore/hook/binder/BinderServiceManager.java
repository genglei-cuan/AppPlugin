package com.cuan.plugincore.hook.binder;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by genglei-cuan on 16-10-22.
 */

public class BinderServiceManager {
    private static Map<String, IBinder> mOriginServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, IBinder> mProxiedServiceCache = new HashMap<String, IBinder>(1);
    private static Map<String, Object> mProxiedObjCache = new HashMap<String, Object>(1);

    static IBinder getOriginService(String serviceName) {
        return mOriginServiceCache.get(serviceName);
    }

    public static void addOriginService(String serviceName, IBinder service) {
        mOriginServiceCache.put(serviceName, service);
    }

    static  void addProxiedServiceCache(String serviceName, IBinder proxyService) {
        mProxiedServiceCache.put(serviceName, proxyService);
    }

    static Object getProxiedObj(String servicename) {
        return mProxiedObjCache.get(servicename);
    }

    static void addProxiedObj(String servicename, Object obj) {
        mProxiedObjCache.put(servicename, obj);
    }
}
