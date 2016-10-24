package com.cuan.plugincore.pluginPMS;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by genglei-cuan on 16-10-24.
 */

public class PluginPMSImpl extends IPluginPMS.Stub {
    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws RemoteException {
        return null;
    }

    @Override
    public boolean isPluginPackage(String packageName) throws RemoteException {
        return false;
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName className, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ResolveInfo resolveService(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) throws RemoteException {
        return null;
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException {
        return null;
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException {
        return null;
    }

    @Override
    public ProviderInfo resolveContentProvider(String name, int flags) throws RemoteException {
        return null;
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws RemoteException {
        return null;
    }

    @Override
    public int installPackage(String filepath, int flags) throws RemoteException {
        return 0;
    }

    @Override
    public int deletePackage(String packageName, int flags) throws RemoteException {
        return 0;
    }

    @Override
    public List<ActivityInfo> getReceivers(String packageName, int flags) throws RemoteException {
        return null;
    }

    @Override
    public List<IntentFilter> getReceiverIntentFilter(ActivityInfo info) throws RemoteException {
        return null;
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) throws RemoteException {
        return 0;
    }
}
