package com.cuan.plugincore.compat;

import android.os.IBinder;

import com.cuan.helper.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by genglei-cuan on 16-10-22.
 */

public class ServiceManagerCompat {
    private static Class sClass = null;

    public static final Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.os.ServiceManager");
        }
        return sClass;
    }


    public static IBinder getService(String name) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (IBinder) MethodUtils.invokeStaticMethod(Class(), "getService", name);
    }
}
