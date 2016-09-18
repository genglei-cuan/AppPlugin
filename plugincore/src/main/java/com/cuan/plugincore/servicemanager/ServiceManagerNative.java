package com.cuan.plugincore.servicemanager;

import android.database.MatrixCursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.HashMap;

/**
 * Created by genglei-cuan on 16-9-18.
 */

/**
 * 主要负责App service biner的注册和查询,类似系统中的ServiceManager
 */
public class ServiceManagerNative extends Binder implements IServiceManager {

    private static final java.lang.String DESCRIPTOR = ServiceManagerProxy.DESCRIPTOR;

    /**
     * Construct the stub at attach it to the interface.
     */
    public ServiceManagerNative() {
        this.attachInterface(this, DESCRIPTOR);
    }


    @Override
    public android.os.IBinder asBinder() {
        return this;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
            throws android.os.RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_getService: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                IBinder _result = getService(_arg0);
                reply.writeNoException();
                reply.writeStrongBinder(_result);
                return true;
            }
            case TRANSACTION_checkService: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                IBinder _result = checkService(_arg0);
                reply.writeNoException();
                reply.writeStrongBinder(_result);
                return true;
            }
            case TRANSACTION_addService: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                IBinder _arg1;
                _arg1 = data.readStrongBinder();
                addService(_arg0, _arg1);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_removeService: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                removeService(_arg0);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_listServices: {
                data.enforceInterface(DESCRIPTOR);
                String[] _result = listServices();
                reply.writeNoException();
                reply.writeStringArray(_result);
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    private static HashMap<String, IBinder> sServices = new HashMap<String, IBinder>();
    private static ServiceManagerNative sServiceManagerNative = new ServiceManagerNative() {
        @Override
        public IBinder getService(String name) throws RemoteException {
            return sServices.get(name);
        }

        /**
         * 暂时和getService一致
         * TODO: 添加checkService的逻辑
         */
        @Override
        public IBinder checkService(String name) throws RemoteException {
            return sServices.get(name);
        }

        @Override
        public void addService(String name, IBinder service) throws RemoteException {
            sServices.put(name, service);
        }

        @Override
        public void removeService(String name) throws RemoteException {
            sServices.remove(name);
        }

        @Override
        public String[] listServices() throws RemoteException {
            return (String[]) sServices.keySet().toArray();
        }
    };

    public static MatrixCursor sServiceManagerCursor = new MatrixCursor(new String[]{}) {

        @Override
        public Bundle getExtras() {
            Bundle extra = new Bundle();
            extra.putParcelable(ServiceManager.KEY_SERVICE_MANAGER, new ParcelBinder(sServiceManagerNative));

            return extra;
        }
    };

    /**
     * 空壳
     */
    @Override
    public IBinder getService(String name) throws RemoteException {
        return null;
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        return null;
    }

    @Override
    public void addService(String name, IBinder service) throws RemoteException {

    }

    @Override
    public void removeService(String name) throws RemoteException {

    }

    @Override
    public String[] listServices() throws RemoteException {
        return new String[0];
    }
}
