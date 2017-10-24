package com.yishuqiao.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ListDataSharedPreferences {

    private static final String DB_NAME = "Data";
    private static final String TALK = "talk";
    private static final String PRAISE = "praise";
    private static final String SHOW = "show";
    private static final String ISFAV = "isfav";
    private static final String SHARE = "share";
    private static final String ISSP = "isSP";
    private static final String ISFOLLOW = "isfollow";
    private static final String COLLECT = "collect";
    private SharedPreferences sharedPreferences;

    public ListDataSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
    }

    public void saveTalk(String userpic) {
        sharedPreferences.edit().putString(TALK, userpic).commit();
    }

    public String readTalk() {
        return sharedPreferences.getString(TALK, "0");
    }

    public void savePraise(String praise) {
        sharedPreferences.edit().putString(PRAISE, praise).commit();
    }

    public String readPraise() {
        return sharedPreferences.getString(PRAISE, "0");
    }

    public void saveShow(String show) {
        sharedPreferences.edit().putString(SHOW, show).commit();
    }

    public String readShow() {
        return sharedPreferences.getString(SHOW, "0");
    }

    public void saveIsfav(String isfav) {
        sharedPreferences.edit().putString(ISFAV, isfav).commit();
    }

    public String readIsfav() {
        return sharedPreferences.getString(ISFAV, "0");
    }

    public void saveShare(String share) {
        sharedPreferences.edit().putString(SHARE, share).commit();
    }

    public String readShare() {
        return sharedPreferences.getString(SHARE, "0");
    }

    public void saveIssp(String issp) {
        sharedPreferences.edit().putString(ISSP, issp).commit();
    }

    public String readIssp() {
        return sharedPreferences.getString(ISSP, "0");
    }

    public void saveIsfollow(String Isfollow) {
        sharedPreferences.edit().putString(ISFOLLOW, Isfollow).commit();
    }

    public String readIsfollow() {
        return sharedPreferences.getString(ISFOLLOW, "0");
    }

    public void saveCollect(String collect) {
        sharedPreferences.edit().putString(COLLECT, collect).commit();
    }

    public String readCollect() {
        return sharedPreferences.getString(COLLECT, "0");
    }

}
