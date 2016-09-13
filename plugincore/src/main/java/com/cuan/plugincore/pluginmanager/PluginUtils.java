package com.cuan.plugincore.pluginmanager;

/**
 * Created by genglei.cuan on 16/9/11.
 * genglei.cuan@godinsec.com
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import com.cuan.helper.log.LogUtil;
import com.cuan.helper.reflect.Reflect;
import com.cuan.plugincore.plugin.PluginException;
import com.cuan.plugincore.plugin.PluginInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

    /**
     * 从APK文件解析PackageInfo信息
     */

    public static PackageInfo parseApk(Context hostContext, String apkPath) {
        PackageManager packageManager = hostContext.getPackageManager();
        int flags = 0xffffff;
        PackageInfo info = null;
        /**
         * Android 7.0中该方法第二个参数使用了注解进行检查,传入0xffffff无法编译通过
         */
        info = Reflect.on(packageManager).call("getPackageArchiveInfo",apkPath,flags).get();
        if (info == null) {
            /**
             * 去掉获取签名信息到PackageInfo
             * 在SDK5.0后的版本，如果不签名的APK getPackageArchiveInfo会返回Null
             */
            info = Reflect.on(packageManager).call("getPackageArchiveInfo",apkPath,flags ^ PackageManager.GET_SIGNATURES).get();
        }
        return info;
    }
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
    /**
     * 获取插件的签名
     *
     * 没有通过getPackageManager().getPackageArchiveInfo(,PackageManager.GET_SIGNATURES)
     * 获取插件的签名信息,是因为该方法在有些Android版本中存在bug,没有处理GET_SIGNATURES
     *
     * @param sourcePath
     * @param simpleMode
     * @return
     */
    public static Signature[] collectCertificates(String sourcePath, boolean simpleMode) {
        Signature mSignatures[] = null;
        WeakReference<byte[]> readBufferRef;
        byte[] readBuffer = null;

        try {
            JarFile jarFile = new JarFile(sourcePath);
            Certificate[] certs = null;
            if (simpleMode) {
                // if SIMPLE MODE,, then we
                // can trust it...  we'll just use the AndroidManifest.xml
                // to retrieve its signatures, not validating all of the
                // files.
                JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
                certs = loadCertificates(jarFile, jarEntry, readBuffer);
                if (certs == null) {
                    LogUtil.w("Package "
                            + " has no certificates at entry "
                            + jarEntry.getName() + "; ignoring!");
                    jarFile.close();

                    LogUtil.w("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                    return null;
                }
                if (PluginConstants.DEBUG) {
                    LogUtil.v("File " + sourcePath + ": entry=" + jarEntry
                            + " certs=" + (certs != null ? certs.length : 0));
                    if (certs != null) {
                        final int N = certs.length;
                        for (int i = 0; i < N; i++) {
                            LogUtil.d("  Public key: "
                                    + certs[i].getPublicKey().getEncoded()
                                    + " " + certs[i].getPublicKey());
                        }
                    }
                }
            } else {
                Enumeration entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = (JarEntry) entries.nextElement();
                    if (je.isDirectory()) continue;
                    if (je.getName().startsWith("META-INF/")) continue;
                    Certificate[] localCerts = loadCertificates(jarFile, je,
                            readBuffer);
                    if (PluginConstants.DEBUG) {
                        LogUtil.v("File " + sourcePath + " entry " + je.getName()
                                + ": certs=" + certs + " ("
                                + (certs != null ? certs.length : 0) + ")");
                    }
                    if (localCerts == null) {
                        LogUtil.w("Package "
                                + " has no certificates at entry "
                                + je.getName() + "; ignoring!");
                        jarFile.close();

                        LogUtil.w("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                        return null;
                    } else if (certs == null) {
                        certs = localCerts;
                    } else {
                        // Ensure all certificates match.
                        for (int i = 0; i < certs.length; i++) {
                            boolean found = false;
                            for (int j = 0; j < localCerts.length; j++) {
                                if (certs[i] != null &&
                                        certs[i].equals(localCerts[j])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found || certs.length != localCerts.length) {
                                LogUtil.w("Package "
                                        + " has mismatched certificates at entry "
                                        + je.getName() + "; ignoring!");
                                jarFile.close();

                                LogUtil.w("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES");
                                return null;
                            }
                        }
                    }
                }
            }
            jarFile.close();
            if (certs != null && certs.length > 0) {
                final int N = certs.length;
                mSignatures = new Signature[certs.length];
                for (int i = 0; i < N; i++) {
                    mSignatures[i] = new Signature(
                            certs[i].getEncoded());
                }
            } else {
                LogUtil.w("Package "
                        + " has no certificates; ignoring!");
                LogUtil.w("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                return null;
            }
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
            LogUtil.w("Exception reading " + sourcePath);
            LogUtil.w("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.w("Exception reading " + sourcePath);
            LogUtil.w("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtil.w("Exception reading " + sourcePath);
            LogUtil.w("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION");
            return null;
        }
        return mSignatures;
    }
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            InputStream is = new BufferedInputStream(jarFile.getInputStream(je));
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.w("Exception reading " + je.getName() + " in "
                    + jarFile.getName());
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtil.w("Exception reading " + je.getName() + " in "
                    + jarFile.getName());
        }
        return null;
    }
    /**
     * 将plugin APK文件拷贝到安装目录,同时将so库解压到沙箱目录
     *
     * TODO: so库是放到安装目录还是沙箱目录呢?
     */
    public static void copyAndExtractApkFile(PluginInfo info,String apkFilePath) throws Exception {
        // 复制APK到安装目录中
        try {
            PluginUtils.copyApk(apkFilePath, info.getPluginPath());
        } catch (Exception e) {
            throw new PluginException(PluginException.ERROR_CODE_COPY_FILE_APK, e.getMessage());
        }
        // 复制APK中的so库文件到plugin的LIB目录
        try {
            PluginUtils.copyLibs(apkFilePath, info.getLibraryPath());
        } catch (Exception e) {
            throw new PluginException(PluginException.ERROR_CODE_COPY_FILE_SO, e.getMessage());
        }
    }


    public static PluginInfo makePluginInfoFromRealm(PluginInfo info) {
        PluginInfo newInfo = new PluginInfo();
        newInfo.setId(info.getId());
        newInfo.setVersion(info.getVersion());
        newInfo.setPackageName(info.getPackageName());
        newInfo.setPluginPath(info.getPluginPath());
        newInfo.setPluginDataDir(info.getPluginDataDir());
        newInfo.setType(info.getType());
        newInfo.setLibraryPath(info.getLibraryPath());
        newInfo.setSelfPlugin(info.isSelfPlugin());
        return newInfo;
    }
}
