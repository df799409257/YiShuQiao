package com.yishuqiao.utils;

public class Conn {

    /**
     * 微信分享ID
     */
    public static String APP_ID = "wxf00db2174c4b1c6a";
    public static final String WEIBO_ID = "2408901555";           // 应用的APP_KEY
    public static final String REDIRECT_URL = "http://www.sina.com";// 应用的回调页
    public static final String SCOPE =                               // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    public static final boolean IsOkhttp = true;
    public static final boolean IsNetWork = false;
    public static String sdCardPathDown = "";

    public static final String PagerSize = "12";// 分页加载数据的每页数据量

    public static String PublishMessage = "";// 发布图文时的文字信息

}
