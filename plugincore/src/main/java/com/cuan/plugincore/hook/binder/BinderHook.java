package com.cuan.plugincore.hook.binder;

import android.text.TextUtils;

import com.cuan.helper.MyProxy;
import com.cuan.helper.reflect.Utils;
import com.cuan.plugincore.hook.base.Hook;
import com.cuan.plugincore.hook.base.HookedMethodHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by genglei-cuan on 16-10-22.
 */

/**
 * 对于系统 service 的 hook 需要生成两个动态代理对象:
 *
 * 1. service 的新的 IBinder 对象
 * 2. service 的新的 代理对象
 */
public abstract class BinderHook extends Hook implements InvocationHandler{

    // service 原代理对象
    private Object mOldObj;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (!isEnable()) {
                return method.invoke(mOldObj, args);
            }
            HookedMethodHandler hookedMethodHandler = mHookHandle.getHookedMethodHandler(method.getName());
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(mOldObj, method, args);
            } else {
                return method.invoke(mOldObj, args);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause != null && MyProxy.isMethodDeclaredThrowable(method, cause)) {
                throw cause;
            } else if (cause != null) {
                RuntimeException runtimeException = !TextUtils.isEmpty(cause.getMessage()) ? new RuntimeException(cause.getMessage()) : new RuntimeException();
                runtimeException.initCause(cause);
                throw runtimeException;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        } catch (IllegalArgumentException e) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(" APP-PLUGIN{");
                if (method != null) {
                    sb.append("method[").append(method.toString()).append("]");
                } else {
                    sb.append("method[").append("NULL").append("]");
                }
                if (args != null) {
                    sb.append("args[").append(Arrays.toString(args)).append("]");
                } else {
                    sb.append("args[").append("NULL").append("]");
                }
                sb.append("}");

                String message = e.getMessage() + sb.toString();
                throw new IllegalArgumentException(message, e);
            } catch (Throwable e1) {
                throw e;
            }
        } catch (Throwable e) {
            if (MyProxy.isMethodDeclaredThrowable(method, e)) {
                throw e;
            } else {
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        }
    }
    /**
     * 获取 service 原代理对象
     * @return
     * @throws Exception
     */
    protected abstract Object getOldObj() throws Exception;


    void setOldObj(Object mOldObj) {
        this.mOldObj = mOldObj;
    }
    /**
     * 获取 service 名字
     * @return
     */
    public abstract String getServiceName();

    /**
     * 创建 service 新的代理对象,并存入BinderServiceManager.ProxiedObj
     * 当执行动态创建的 service 的新 IBinder 对象的 queryLocalInterface()
     * 方法时便可以得到新的代理对象了.
     * @param classLoader
     * @throws Throwable
     */
    @Override
    public void onInstall(ClassLoader classLoader) throws Throwable {
        new ServiceManagerCacheBinderHook(getServiceName()).onInstall(classLoader);
        mOldObj = getOldObj();
        Class<?> clazz = mOldObj.getClass();
        List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
        Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
        Object proxiedObj = MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
        BinderServiceManager.addProxiedObj(getServiceName(), proxiedObj);
    }
}
