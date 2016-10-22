package com.cuan.plugincore.hook.binder;

import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;

import com.cuan.helper.MyProxy;
import com.cuan.helper.reflect.FieldUtils;
import com.cuan.helper.reflect.Utils;
import com.cuan.plugincore.compat.ServiceManagerCompat;
import com.cuan.plugincore.hook.base.BaseHookHandle;
import com.cuan.plugincore.hook.base.Hook;
import com.cuan.plugincore.hook.base.HookedMethodHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by genglei-cuan on 16-10-22.
 */

/**
 * 目地是将利用java动态代理技术新产生的IBinder对象,存储到ServiceManager.sCache中,
 * 并hook其queryLocalInterface()使其返回新的 service 代理对象,进而达到劫持系统 service 的目地.
 */
public class ServiceManagerCacheBinderHook extends Hook implements InvocationHandler {

    private String mServiceName;

    public ServiceManagerCacheBinderHook(String mServiceName){
        this.mServiceName = mServiceName;
    }

    /**
     * 利用java动态代理技术新产生的IBinder对象,存储到ServiceManager.sCache中,
     * @param classLoader
     * @throws Throwable
     */
    @Override
    public void onInstall(ClassLoader classLoader) throws Throwable {
        Object sCacheObj = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sCache");
        if (sCacheObj instanceof Map) {
            Map sCache = (Map) sCacheObj;
            Object Obj = sCache.get(mServiceName);
            if (Obj != null && false) {
                // 360 DP 框架中指出这里有BUG,所以采用下个分支
                throw new RuntimeException("Can not install binder hook for " + mServiceName);
            } else {
                sCache.remove(mServiceName);
                // 获取原IBinder
                IBinder mServiceIBinder = ServiceManagerCompat.getService(mServiceName);
                if (mServiceIBinder != null) {
                    BinderServiceManager.addOriginService(mServiceName, mServiceIBinder);
                    Class clazz = mServiceIBinder.getClass();
                    List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
                    Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                    // 新产生一个IBinder对象
                    IBinder mProxyServiceIBinder = (IBinder) MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
                    sCache.put(mServiceName, mProxyServiceIBinder);
                    BinderServiceManager.addProxiedServiceCache(mServiceName, mProxyServiceIBinder);
                }
            }
        }
    }

    @Override
    public void onUnInstall(ClassLoader classLoader) throws Throwable {

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            /**
             * 拿到service 的原 IBinder 对象;
             *如果没开启hook,就直接使用原 IBinder 对象执行方法.
             */
            IBinder originService = BinderServiceManager.getOriginService(mServiceName);
            if (!isEnable()) {
                return method.invoke(originService, args);
            }
            /**
             * 已经开启hook;
             * 以方法名如果能获取对于的Handler,说明该方法被hook了
             * 反之说明该方法没有被hook
             */
            HookedMethodHandler hookedMethodHandler = mHookHandle.getHookedMethodHandler(method.getName());
            if (hookedMethodHandler != null) {
                return hookedMethodHandler.doHookInner(originService, method, args);
            } else {
                return method.invoke(originService, args);
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
            /**
             * 如果执行的方法本身就允许抛出异常,那么直接将该异常抛出
             */
            if (MyProxy.isMethodDeclaredThrowable(method, e)) {
                throw e;
            } else {
                /**
                 * 如果执行的方法本身不允许抛出异常,那么说明是由于hook引起的,所以抛出一个运行时异常
                 */
                RuntimeException runtimeException = !TextUtils.isEmpty(e.getMessage()) ? new RuntimeException(e.getMessage()) : new RuntimeException();
                runtimeException.initCause(e);
                throw runtimeException;
            }
        }
    }
    private class ServiceManagerHookHandle extends BaseHookHandle {

        @Override
        protected void init() {
            mHookedMethodHandlers.put("queryLocalInterface", new queryLocalInterface());
        }


        class queryLocalInterface extends HookedMethodHandler {


            @Override
            protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
                Object localInterface = invokeResult;
                // 返回创建的自定义service代理对象
                Object proxiedObj = BinderServiceManager.getProxiedObj(mServiceName);
                if (localInterface == null && proxiedObj != null) {
                    mFakedResult = proxiedObj;
                }
            }
        }
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ServiceManagerHookHandle();
    }

}
