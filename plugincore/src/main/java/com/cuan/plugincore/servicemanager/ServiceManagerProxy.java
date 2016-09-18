package com.cuan.plugincore.servicemanager;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by genglei-cuan on 16-9-18.
 */

public class ServiceManagerProxy implements IServiceManager {



    private android.os.IBinder mRemote;

    ServiceManagerProxy(android.os.IBinder remote) {
        mRemote = remote;
    }

    /**
     * App进程中首先从Provider中查询出ServiceManager的IBinder对象;
     * 然后通过下面的这个该方法可将IBinder对象转换为binder实体对象或者代理对象了.
     */
    public static IServiceManager asInterface(IBinder obj) {
        if ((obj == null)) {
            return null;
        }
        android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (((iin != null) && (iin instanceof IServiceManager))) {
            return ((IServiceManager) iin);
        }
        return new ServiceManagerProxy(obj);
    }

    @Override
    public IBinder asBinder() {
        return mRemote;
    }

    public String getInterfaceDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public IBinder getService(String name) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(name);
            mRemote.transact(TRANSACTION_getService, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(name);
            mRemote.transact(TRANSACTION_checkService, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    public void addService(String name, IBinder service) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(name);
            _data.writeStrongBinder(service);
            mRemote.transact(TRANSACTION_addService, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    @Override
    public void removeService(String name) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(name);
            mRemote.transact(TRANSACTION_removeService, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    @Override
    public String[] listServices() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        String[] _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            mRemote.transact(TRANSACTION_listServices, _data, _reply, 0);
            _reply.readException();
            _result = _reply.createStringArray();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }



}
