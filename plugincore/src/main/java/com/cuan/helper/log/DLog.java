package com.cuan.helper.log;

/**
 * Created by genglei.cuan on 2016/10/21.
 * genglei.cuan@godinsec.com
 */

public class DLog {
    private static boolean mEnable = true;

    public static void i(String tag, String message, Object... args){
        if(mEnable) {
            if(args != null && args.length>0)
                message = String.format(message,args);
            android.util.Log.i(tag,message);
        }
    }

    public static void e(String tag,String message,Object... args){
        if(mEnable){
            if(args != null && args.length>0)
                message = String.format(message,args);
            android.util.Log.e(tag,message);
        }
    }
    public static void e(String tag,String message,Throwable tr,Object... args){
        if(mEnable){
            if(args != null && args.length>0)
                message = String.format(message,args);
            android.util.Log.e(tag,message,tr);
        }
    }
}
