package com.cuan.plugincore.servicemanager;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * Created by genglei-cuan on 16-9-18.
 */

/**
 * 实现一个App管理自己多进程中的service的简易manager.
 *
 * 提供App自己的其他进程中service的注册与查询使用,类似系统ServiceManger.
 *
 * 其中一个作用便是可以规避aidl的使用,这样可以更灵活.(当目前的这个简易框架需要手写代码较多哦)
 *
 * 另一个作用是存储系统service的IBinder对象.
 *
 * App的所有进程都可以通过ContentProvider来获取这个manager.
 */
public interface IServiceManager extends IInterface{

     IBinder getService(String name) throws RemoteException;

     IBinder checkService(String name) throws RemoteException;

     void addService(String name, IBinder service) throws RemoteException;

     void removeService(String name) throws RemoteException;

     String[] listServices() throws RemoteException;

     int TRANSACTION_getService = (IBinder.FIRST_CALL_TRANSACTION + 0);
     int TRANSACTION_checkService = (IBinder.FIRST_CALL_TRANSACTION + 1);
     int TRANSACTION_addService = (IBinder.FIRST_CALL_TRANSACTION + 2);
     int TRANSACTION_removeService = (IBinder.FIRST_CALL_TRANSACTION + 3);
     int TRANSACTION_listServices = (IBinder.FIRST_CALL_TRANSACTION + 4);

     String DESCRIPTOR = "com.cuan.plugincore.servicemanager.IServiceManager";
}
