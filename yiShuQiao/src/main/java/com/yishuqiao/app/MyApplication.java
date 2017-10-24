package com.yishuqiao.app;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.qiniu.android.storage.UploadManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.yishuqiao.R;
import com.yishuqiao.utils.CustomConstants;
import com.yishuqiao.utils.ListDataSharedPreferences;
import com.yishuqiao.utils.ZSharedPreferences;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    public static ListDataSharedPreferences sharedPreferences;
    public static ZSharedPreferences MySharedPreferences;
    private static List<Activity> mList = new LinkedList<Activity>();

    private static MyApplication mInstance;

    public static UploadManager uploadManager = null;
    public static boolean isfull = false;
    private static MyApplication application;

    @Override
    public void onCreate() {
        // 极光推送相关
        JPushInterface.setDebugMode(false);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        sharedPreferences = new ListDataSharedPreferences(this);
        MySharedPreferences = new ZSharedPreferences(this);
        mInstance = this;
        removeTempFromPref();

        initImageloader();

        x.Ext.init(this);

        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }

        // 捕获异常信息
        // CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext(), "b916c638d5", false);

        application = this;

    }

    public static UploadManager getConfiguration() {
        return uploadManager;

    }

    public void initImageloader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_bg)
                .showImageOnFail(R.drawable.icon_error).resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        File picPath = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "ArtBridge"
                + File.separator + "picture");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
                // default
                .diskCache(new UnlimitedDiskCache(picPath))
                // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(1000)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                // default
                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(options) // default
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    public void finishActivity() {
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void removeActivity(int position) {
        mList.remove(position);
    }

    /**
     * delSerFile(删除序列化缓存文件)
     *
     * @param @return 设定文件
     * @return String DOM对象
     * @Exception 异常对象
     * @since CodingExample Ver(编码范例查看) 1.1
     */
    public void DelSerFile() {
        File[] files = getSerFileDir().listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(".ser")) {
                files[i].delete();
            }
        }
    }

    /**
     * getSerFileDir(序列化缓存文件路径)
     *
     * @param @return 设定文件
     * @return String DOM对象
     * @Exception 异常对象
     * @since CodingExample Ver(编码范例查看) 1.1
     */
    public File getSerFileDir() {
        return getApplicationContext().getFilesDir();
    }

    public void DelImgCacheDataFile() {

        File imgCacheDataPath = new File(getApplicationContext().getCacheDir(), "picasso-cache");
        File[] picasso_files = imgCacheDataPath.listFiles();
        for (int i = 0; i < picasso_files.length; i++) {
            picasso_files[i].delete();
        }
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static Context getContext() {
        return application;
    }

    // 构造方法
    // 实例化一次
    public synchronized static MyApplication getInstance() {
        if (null == mInstance) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
