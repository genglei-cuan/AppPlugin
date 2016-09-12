package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 插件的相关操作：
 *
 * 1. apk的拷贝
 *
 * 2. apk中so库的提取
 *
 * 3. apk沙箱目录的创建与删除
 *
 * 4. 解析apk
 *
 * 5.
 *
 * TODO: 类在的CPU_ABI是一个与Android密切相关的变量，后续需要认真适配，暂时使用废弃的api
 */

public class PluginUtils {


    /**
     * 暂时使用这个过时的api
     */
    private static final String CPU_ABI = Build.CPU_ABI;

    /*
    private static final String CPU_ABI_32[] = Build.SUPPORTED_32_BIT_ABIS;
    private static final String CPU_ABI_64[] = Build.SUPPORTED_64_BIT_ABIS;
    */
    private static void copyFile(File source,File dest) throws IOException{
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
    /**
     * 复到APK到指定目录
     * @param apkPath
     * @param desApkPath
     * @throws Exception
     *
     * TODO:现阶段使用的文件通道拷贝文件，如有必要，后续考虑JNI实现。
     */
    public static void copyApk(String apkPath, String desApkPath) throws IOException {
        File source = new File(apkPath);
        if (!source.exists()) {
            return;
        }
        File dest = new File(desApkPath);
        if(dest.exists()){
            dest.delete();
        }
       copyFile(source,dest);
    }

    /**
     * 复到so库到指定目录
     * @param apkPath
     * @param libPath
     * @throws Exception
     *
     * TODO: 后续可以考虑JNI实现,以提高效率
     */
    public static void copyLibs(String apkPath, String libPath) throws Exception {
        ZipFile zipFile = new ZipFile(apkPath);
        Enumeration<?> e = zipFile.entries();
        Set<String> exactLibNames = new HashSet<String>();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            String entryName = entry.getName();
            if (entryName.endsWith(".so") &&
                    (entryName.contains("armeabi") || entryName.contains(CPU_ABI))) {
                String libName = entryName.substring(entryName.lastIndexOf("/"));
                if (entryName.contains(CPU_ABI)) {
                    //和本机CPU_ABI一样
                    exactLibNames.add(libName);
                } else if (exactLibNames.contains(libName)) {
                    // 非精确的so，可以是armeabi或者armeabi-v7a, 且前面已经加载过精确的so了
                    continue;
                }
                File outFile = new File(libPath + File.separator + libName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                InputStream in = zipFile.getInputStream(entry);
                File file =  new File(in.toString());
                FileOutputStream out = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.close();
                in.close();
            }
        }
        zipFile.close();
    }

}
