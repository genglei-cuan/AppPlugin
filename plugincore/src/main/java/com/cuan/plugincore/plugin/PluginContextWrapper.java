package com.cuan.plugincore.plugin;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;

import com.cuan.plugincore.pluginmanager.PluginManager;

import java.io.File;

/**
 * Created by genglei-cuan on 16-9-12.
 */

public class PluginContextWrapper extends ContextWrapper {
    Context hostContext;

    public PluginContextWrapper(Context base) {
        super(base);
        this.hostContext = PluginManager.getInstance().getHostContext();
    }

    @Override
    public ContentResolver getContentResolver() {
        return hostContext.getContentResolver();
    }

    @Override
    public Object getSystemService(String name) {
        if (PluginManager.useHostSystemService(name)) {
            return hostContext.getSystemService(name);
        }
        return super.getSystemService(name);
    }

    @Override
    public File getExternalFilesDir(String type) {
        return hostContext.getExternalFilesDir(type);
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        return hostContext.getExternalFilesDirs(type);
    }

    @Override
    public File getExternalCacheDir() {
        return hostContext.getExternalCacheDir();
    }

    @Override
    public File[] getExternalCacheDirs() {
        return hostContext.getExternalCacheDirs();
    }
}
