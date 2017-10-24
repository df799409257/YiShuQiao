package com.yishuqiao.utils;

import com.yishuqiao.app.MyApplication;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ListViewDateUtils
 * @Description TODO(存储和读取listview的原始值和新值)
 * @Date 2017年8月10日 下午6:21:16
 */
public class ListViewDataUtils {

    public static void saveTalk(String talk) {
        MyApplication.sharedPreferences.saveTalk(talk);
    }

    public static void savePraise(String praise) {
        MyApplication.sharedPreferences.savePraise(praise);
    }

    public static void saveShow(String show) {
        MyApplication.sharedPreferences.saveShow(show);
    }

    public static void saveIsfav(String isfav) {
        MyApplication.sharedPreferences.saveIsfav(isfav);
    }

    public static void saveShare(String share) {
        MyApplication.sharedPreferences.saveShare(share);
    }

    public static void saveIsSP(String isSP) {
        MyApplication.sharedPreferences.saveIssp(isSP);
    }

    public static void saveIsfollow(String isfollow) {
        MyApplication.sharedPreferences.saveIsfollow(isfollow);
    }

    public static void saveCollect(String collect) {
        MyApplication.sharedPreferences.saveCollect(collect);
    }

    public static String readTalk() {
        return MyApplication.sharedPreferences.readTalk();
    }

    public static String readPraise() {
        return MyApplication.sharedPreferences.readPraise();
    }

    public static String readShow() {
        return MyApplication.sharedPreferences.readShow();
    }

    public static String readIsfav() {
        return MyApplication.sharedPreferences.readIsfav();
    }

    public static String readShare() {
        return MyApplication.sharedPreferences.readShare();
    }

    public static String readIssp() {
        return MyApplication.sharedPreferences.readIssp();
    }

    public static String readIsfollow() {
        return MyApplication.sharedPreferences.readIsfollow();
    }

    public static String readCollect() {
        return MyApplication.sharedPreferences.readCollect();
    }

}
