package com.cuan.plugincore.pluginmanager.stub;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

/**
 * Created by genglei-cuan on 16-10-24.
 */


public class PluginProcessProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }


    public static Bundle call(String authority, String methodName, String arg, Bundle bundle) {
        Uri uri = Uri.parse("content://" + authority);
        ContentResolver contentResolver = null;
        return contentResolver.call(uri, methodName, arg, bundle);
    }
    /**
     * 当该方法返回的时候,进程已经创建完毕了.
     * @param method
     * @param arg
     * @param extras
     * @return
     */
    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if ("init_process".equals(method)) {
            return initProcess(extras);
        }
        return null;
    }

    private Bundle initProcess(Bundle extras) {

        return null;
    }


    public static class C0 extends PluginProcessProvider {
    }

    public static class C1 extends PluginProcessProvider {
    }

    public static class C2 extends PluginProcessProvider {
    }

    public static class C3 extends PluginProcessProvider {
    }

    public static class C4 extends PluginProcessProvider {
    }

    public static class C5 extends PluginProcessProvider {
    }

    public static class C6 extends PluginProcessProvider {
    }

    public static class C7 extends PluginProcessProvider {
    }

    public static class C8 extends PluginProcessProvider {
    }

    public static class C9 extends PluginProcessProvider {
    }

    public static class C10 extends PluginProcessProvider {
    }

    public static class C11 extends PluginProcessProvider {
    }

    public static class C12 extends PluginProcessProvider {
    }

    public static class C13 extends PluginProcessProvider {
    }

    public static class C14 extends PluginProcessProvider {
    }

    public static class C15 extends PluginProcessProvider {
    }

    public static class C16 extends PluginProcessProvider {
    }

    public static class C17 extends PluginProcessProvider {
    }

    public static class C18 extends PluginProcessProvider {
    }

    public static class C19 extends PluginProcessProvider {
    }

    public static class C20 extends PluginProcessProvider {
    }

    public static class C21 extends PluginProcessProvider {
    }

    public static class C22 extends PluginProcessProvider {
    }

    public static class C23 extends PluginProcessProvider {
    }

    public static class C24 extends PluginProcessProvider {
    }

    public static class C25 extends PluginProcessProvider {
    }

    public static class C26 extends PluginProcessProvider {
    }

    public static class C27 extends PluginProcessProvider {
    }

    public static class C28 extends PluginProcessProvider {
    }

    public static class C29 extends PluginProcessProvider {
    }

    public static class C30 extends PluginProcessProvider {
    }

    public static class C31 extends PluginProcessProvider {
    }

    public static class C32 extends PluginProcessProvider {
    }

    public static class C33 extends PluginProcessProvider {
    }

    public static class C34 extends PluginProcessProvider {
    }

    public static class C35 extends PluginProcessProvider {
    }

    public static class C36 extends PluginProcessProvider {
    }

    public static class C37 extends PluginProcessProvider {
    }

    public static class C38 extends PluginProcessProvider {
    }

    public static class C39 extends PluginProcessProvider {
    }

    public static class C40 extends PluginProcessProvider {
    }

    public static class C41 extends PluginProcessProvider {
    }

    public static class C42 extends PluginProcessProvider {
    }

    public static class C43 extends PluginProcessProvider {
    }

    public static class C44 extends PluginProcessProvider {
    }

    public static class C45 extends PluginProcessProvider {
    }

    public static class C46 extends PluginProcessProvider {
    }

    public static class C47 extends PluginProcessProvider {
    }

    public static class C48 extends PluginProcessProvider {
    }

    public static class C49 extends PluginProcessProvider {
    }

    public static class C50 extends PluginProcessProvider {
    }

    public static class C51 extends PluginProcessProvider {
    }

    public static class C52 extends PluginProcessProvider {
    }

    public static class C53 extends PluginProcessProvider {
    }

    public static class C54 extends PluginProcessProvider {
    }

    public static class C55 extends PluginProcessProvider {
    }

    public static class C56 extends PluginProcessProvider {
    }

    public static class C57 extends PluginProcessProvider {
    }

    public static class C58 extends PluginProcessProvider {
    }

    public static class C59 extends PluginProcessProvider {
    }

    public static class C60 extends PluginProcessProvider {
    }

    public static class C61 extends PluginProcessProvider {
    }

    public static class C62 extends PluginProcessProvider {
    }

    public static class C63 extends PluginProcessProvider {
    }

    public static class C64 extends PluginProcessProvider {
    }

    public static class C65 extends PluginProcessProvider {
    }

    public static class C66 extends PluginProcessProvider {
    }

    public static class C67 extends PluginProcessProvider {
    }

    public static class C68 extends PluginProcessProvider {
    }

    public static class C69 extends PluginProcessProvider {
    }

    public static class C70 extends PluginProcessProvider {
    }

    public static class C71 extends PluginProcessProvider {
    }

    public static class C72 extends PluginProcessProvider {
    }

    public static class C73 extends PluginProcessProvider {
    }

    public static class C74 extends PluginProcessProvider {
    }

    public static class C75 extends PluginProcessProvider {
    }

    public static class C76 extends PluginProcessProvider {
    }

    public static class C77 extends PluginProcessProvider {
    }

    public static class C78 extends PluginProcessProvider {
    }

    public static class C79 extends PluginProcessProvider {
    }

    public static class C80 extends PluginProcessProvider {
    }

    public static class C81 extends PluginProcessProvider {
    }

    public static class C82 extends PluginProcessProvider {
    }

    public static class C83 extends PluginProcessProvider {
    }

    public static class C84 extends PluginProcessProvider {
    }

    public static class C85 extends PluginProcessProvider {
    }

    public static class C86 extends PluginProcessProvider {
    }

    public static class C87 extends PluginProcessProvider {
    }

    public static class C88 extends PluginProcessProvider {
    }

    public static class C89 extends PluginProcessProvider {
    }

    public static class C90 extends PluginProcessProvider {
    }

    public static class C91 extends PluginProcessProvider {
    }

    public static class C92 extends PluginProcessProvider {
    }

    public static class C93 extends PluginProcessProvider {
    }

    public static class C94 extends PluginProcessProvider {
    }

    public static class C95 extends PluginProcessProvider {
    }

    public static class C96 extends PluginProcessProvider {
    }

    public static class C97 extends PluginProcessProvider {
    }

    public static class C98 extends PluginProcessProvider {
    }

    public static class C99 extends PluginProcessProvider {
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
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
