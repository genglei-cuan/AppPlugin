package com.cuan.plugincore.servicemanager;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by genglei-cuan on 16-9-18.
 */

/**
 * 方便App中的其他进程获的ServiceManagerNative的IBinder对象
 */
public class ServiceManagerProvider extends ContentProvider {

    private static final int URL_MATCH_SERVICE_MANAGER = 0;
    private static final UriMatcher URI_MATCHER;

    static {
        // 初始化 UriMatcher
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        // 注册需要的Uri
        URI_MATCHER.addURI(ServiceManager.AUTHORITY, ServiceManager.PATH_SERVICE_MANAGER, URL_MATCH_SERVICE_MANAGER);
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // 与前面已经注册的uri进行匹配
        int code = URI_MATCHER.match(uri);
        switch (code) {
            case URL_MATCH_SERVICE_MANAGER:
                return ServiceManagerNative.sServiceManagerCursor;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
