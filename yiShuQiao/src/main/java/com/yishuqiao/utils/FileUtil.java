package com.yishuqiao.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017年7月18日 下午2:14:16
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：FileUtil.java
 * <p>
 * 类说明：保存错误日志
 */
@SuppressLint("SimpleDateFormat")
public class FileUtil {

    public static final String LOGPATH = Utils.CACHEPATH + "log" + File.separator;

    public static void saveLog(String log) {
        if (!TextUtils.isEmpty(log)) {
            Log.i("YiShuQiao", log);
            // 判断手机是否有SD卡
            if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
                final Date date = new Date();
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final SimpleDateFormat timeDateFormat = new SimpleDateFormat("HH:mm:ss");
                final String name = "log" + dateFormat.format(date) + ".txt";
                log = "\r\n" + timeDateFormat.format(date) + ":  " + log;
                saveFile(LOGPATH, name, log, true);
            }
        }
    }

    public static synchronized void saveFile(final String filePath, final String fileName, final String str,
                                             final boolean append) {

        // 判断手机是否有SD卡
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            FileOutputStream fos = null;
            OutputStreamWriter out = null;
            try {
                makeDir(filePath);
                FileOutputStream fout = new FileOutputStream(filePath + fileName, append);
                byte[] bytes = str.getBytes();
                fout.write(bytes);
                fout.close();
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        out = null;
                    }
                    if (fos != null) {
                        fos.close();
                        fos = null;
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    public static void makeDir(final String path) {
        if (path != null && !"".equalsIgnoreCase(path)) {
            File file = new File(path);
            if (!(file.isDirectory())) { // 判断文件夹是否存在
                file.mkdirs(); // 创建文件夹包括创建必需但不存在的父目录
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex ex
     * @return null
     */
    public static void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        FileUtil.saveLog("错误信息:" + result);
    }
}
