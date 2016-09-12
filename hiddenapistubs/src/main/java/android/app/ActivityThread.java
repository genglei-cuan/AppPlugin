package android.app;


import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;

/**
 * Created by genglei-cuan on 16-9-12.
 */

public final class ActivityThread {
    public static ActivityThread currentActivityThread() {
        throw new UnsupportedOperationException("STUB");
    }

    public static Application currentApplication() {
        throw new UnsupportedOperationException("STUB");
    }

    public static String currentPackageName() {
        throw new UnsupportedOperationException("STUB");
    }

    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo) {
        throw new UnsupportedOperationException("STUB");
    }
    public Instrumentation getInstrumentation(){ throw new UnsupportedOperationException("STUB");}
}
