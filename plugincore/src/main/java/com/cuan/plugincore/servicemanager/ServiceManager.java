package com.cuan.plugincore.servicemanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by genglei-cuan on 16-9-18.
 */

/**
 * App的进程可以通过该类注册查询自己注册的service的Ibinder对象.
 */
public class ServiceManager {

    public static final String AUTHORITY = "com.cuan.plugincore.servicemanager.ServiceManagerProvider";
    public static final String PATH_SERVICE_MANAGER = "service_path";
    public static final String KEY_SERVICE_MANAGER = "service_manager";

    private static final Uri URI_SERVICE_MANAGER = Uri.parse("content://" + AUTHORITY + "/" + PATH_SERVICE_MANAGER);

    private static final Object sLocker = new Object();
    private volatile static IServiceManager sServiceManager;

    private static IBinder.DeathRecipient sRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            synchronized (sLocker) {
                sServiceManager = null;
            }
        }
    };

    private static IServiceManager getServiceManager(Context context) {
        synchronized (sLocker) {
            if (sServiceManager == null) {
                sServiceManager = getServiceManagerNative(context);
                try {
                    sServiceManager.asBinder().linkToDeath(sRecipient, 0);
                } catch (RemoteException e) {
                }
            }

            return sServiceManager;
        }
    }

    private static IServiceManager getServiceManagerNative(Context context) {
        IServiceManager manager = null;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(URI_SERVICE_MANAGER, null, null, null, null);
            Bundle bundle = cursor.getExtras();
            bundle.setClassLoader(ParcelBinder.class.getClassLoader());
            ParcelBinder parcel = bundle.getParcelable(KEY_SERVICE_MANAGER);
            IBinder binder = parcel.getBinder();

            manager = ServiceManagerProxy.asInterface(binder);
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }

        return manager;
    }

    public static IBinder getService(Context context, String name) throws RemoteException {
        return getServiceManager(context).getService(name);
    }

    public static void registerService(Context context, String name, IBinder service) throws RemoteException {
        getServiceManager(context).addService(name, service);
    }

    public static void unregisterService(Context context, String name) throws RemoteException {
        getServiceManager(context).removeService(name);
    }
}
