package com.yishuqiao.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ZSharedPreferences {

    private static final String DB_NAME = "SuBu";
    private static final String ISLOGIN = "isloign";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USERID = "userid";
    private static final String GENDER = "gender";
    private static final String TOKEN = "token";
    private static final String YINDAO = "yindao";
    private static final String TEL = "tel";
    private static final String POINTS = "points";
    private static final String REGTIME = "regtime";
    private static final String USERPIC = "userpic";
    private static final String PUSHTURN = "pushturn";
    private static final String WEIBOTOKEN = "weibotoken";
    private static final String FRAGMENT1 = "fragment1";
    private static final String FRAGMENT2 = "fragment2";
    private static final String FRAGMENT3 = "fragment3";
    private static final String FRAGMENT4 = "fragment4";
    private SharedPreferences sharedPreferences;

    public ZSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
    }

    public boolean saveIsLogin(boolean isLogin) {
        return sharedPreferences.edit().putBoolean(ISLOGIN, isLogin).commit();
    }

    public boolean readIsLogin() {
        return sharedPreferences.getBoolean(ISLOGIN, false);
    }

    public boolean saveIsPush(boolean isPush) {
        return sharedPreferences.edit().putBoolean(PUSHTURN, isPush).commit();
    }

    public boolean readIsPush() {
        return sharedPreferences.getBoolean(PUSHTURN, false);
    }

    public boolean saveIsYinDao(boolean isLogin) {
        return sharedPreferences.edit().putBoolean(YINDAO, isLogin).commit();
    }

    public boolean readIsYinDao() {
        return sharedPreferences.getBoolean(YINDAO, true);
    }

    public void saveUserPic(String userpic) {
        sharedPreferences.edit().putString(USERPIC, userpic).commit();
    }

    public String readUserPic() {
        return sharedPreferences.getString(USERPIC, "");
    }

    public void saveRegtime(String regtime) {
        sharedPreferences.edit().putString(REGTIME, regtime).commit();
    }

    public String readRegtime() {
        return sharedPreferences.getString(REGTIME, "");
    }

    public void savePoints(String points) {
        sharedPreferences.edit().putString(POINTS, points).commit();
    }

    public String readPoints() {
        return sharedPreferences.getString(POINTS, "0");
    }

    public void saveGender(String gender) {
        sharedPreferences.edit().putString(GENDER, gender).commit();
    }

    public String readGender() {
        return sharedPreferences.getString(GENDER, "男");
    }

    public void saveUsername(String username) {
        sharedPreferences.edit().putString(USERNAME, username).commit();
    }

    public String readUsername() {
        return sharedPreferences.getString(USERNAME, "");
    }

    public void savePassWord(String password) {
        sharedPreferences.edit().putString(PASSWORD, password).commit();
    }

    public String readPassWord() {
        return sharedPreferences.getString(PASSWORD, "");
    }

    public void saveUserid(String userid) {
        sharedPreferences.edit().putString(USERID, userid).commit();
    }

    public String readUserid() {
        return sharedPreferences.getString(USERID, "");
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).commit();
    }

    public String readToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public void saveTel(String tel) {
        sharedPreferences.edit().putString(TEL, tel).commit();
    }

    public String readTel() {
        return sharedPreferences.getString(TEL, "");
    }

    public void saveWeiBoToken(String weibotoken) {
        sharedPreferences.edit().putString(WEIBOTOKEN, weibotoken).commit();
    }

    public String readWeiBoToken() {
        return sharedPreferences.getString(WEIBOTOKEN, "");
    }

    public void saveFragment1(String fragment1) {
        sharedPreferences.edit().putString(FRAGMENT1, fragment1).commit();
    }

    public String readFragment1() {
        return sharedPreferences.getString(FRAGMENT1, "");
    }

    public void saveFragment2(String fragment2) {
        sharedPreferences.edit().putString(FRAGMENT2, fragment2).commit();
    }

    public String readFragment2() {
        return sharedPreferences.getString(FRAGMENT2, "");
    }

    public void saveFragment3(String fragment3) {
        sharedPreferences.edit().putString(FRAGMENT3, fragment3).commit();
    }

    public String readFragment3() {
        return sharedPreferences.getString(FRAGMENT3, "");
    }

    public void saveFragment4(String fragment4) {
        sharedPreferences.edit().putString(FRAGMENT4, fragment4).commit();
    }

    public String readFragment4() {
        return sharedPreferences.getString(FRAGMENT4, "");
    }
}
