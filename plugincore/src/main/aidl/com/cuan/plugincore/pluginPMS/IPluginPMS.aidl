// IPluginPMS.aidl
package com.cuan.plugincore.pluginPMS;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import java.util.List;

interface IPluginPMS {
     PackageInfo getPackageInfo(in String packageName, int flags);

     boolean isPluginPackage(in String packageName);

     ActivityInfo getActivityInfo(in ComponentName className, int flags);

     ActivityInfo getReceiverInfo(in ComponentName className, int flags);

     ServiceInfo getServiceInfo(in ComponentName className, int flags);

     ProviderInfo getProviderInfo(in ComponentName className, int flags);

     ResolveInfo resolveIntent(in Intent intent, in String resolvedType, int flags);

     List<ResolveInfo> queryIntentActivities(in Intent intent,in  String resolvedType, int flags);

     List<ResolveInfo> queryIntentReceivers(in Intent intent, String resolvedType, int flags);

     ResolveInfo resolveService(in Intent intent, String resolvedType, int flags);

     List<ResolveInfo> queryIntentServices(in Intent intent, String resolvedType, int flags);

     List<ResolveInfo> queryIntentContentProviders(in Intent intent, String resolvedType, int flags);

     List<PackageInfo> getInstalledPackages(int flags);

     List<ApplicationInfo> getInstalledApplications(int flags);

     PermissionInfo getPermissionInfo(in String name, int flags);

     List<PermissionInfo> queryPermissionsByGroup(in String group, int flags);

     PermissionGroupInfo getPermissionGroupInfo(in String name, int flags);

     List<PermissionGroupInfo> getAllPermissionGroups(int flags);

     ProviderInfo resolveContentProvider(in String name, int flags);

    // void deleteApplicationCacheFiles(in String packageName,in  IPackageDataObserver observer);

     //void clearApplicationUserData(in String packageName,in  IPackageDataObserver observer);

     ApplicationInfo getApplicationInfo(in String packageName, int flags);

     int installPackage(in String filepath,int flags);

     int deletePackage(in String packageName ,int flags);

     List<ActivityInfo> getReceivers(in String packageName ,int flags);

     List<IntentFilter> getReceiverIntentFilter(in ActivityInfo info);

     int checkSignatures(in String pkg1, in String pkg2);
}
