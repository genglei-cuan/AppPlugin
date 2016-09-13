package com.cuan.plugincore.plugin;

/**
 * Created by genglei-cuan on 16-9-13.
 */

/**
 * 姑且当做返回值吧,根据异常号就能知道哪里出问题了
 */
public class PluginException extends Exception {
    public final static int	ERROR_CODE_NONE			  = 0; // 成功

    public final static int ERROR_CODE_COPY_FILE_APK  = -30;
    public final static int ERROR_CODE_COPY_FILE_SO   = -29;

    int errorCode;

    public PluginException(int errorCode) {
        this.errorCode = errorCode;
    }

    public PluginException(int errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
