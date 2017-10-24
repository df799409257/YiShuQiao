package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017-7-6 上午10:50:06 项目名称：YiShuQiao 文件名称：MyFansDTO.java 类说明：我的粉丝
 */
public class MyFansDTO {

    private String txt_code;
    private String txt_message;
    private List<FollowInforList> followInforList;

    public List<FollowInforList> getFollowInforList() {
        return followInforList;
    }

    public void setFollowInforList(List<FollowInforList> followInforList) {
        this.followInforList = followInforList;
    }

    public String getTxt_code() {
        return txt_code;
    }

    public void setTxt_code(String txt_code) {
        this.txt_code = txt_code;
    }

    public String getTxt_message() {
        return txt_message;
    }

    public void setTxt_message(String txt_message) {
        this.txt_message = txt_message;
    }

    public class FollowInforList {

        private String txt_imgurl;// 头像地址
        private String txt_nickname;// 用户名
        private String txt_followtime;// 日期
        private String txt_userid;// ID
        private String txt_followme; // 0=未关注 1=互相关注

        public String getTxt_imgurl() {
            return txt_imgurl;
        }

        public void setTxt_imgurl(String txt_imgurl) {
            this.txt_imgurl = txt_imgurl;
        }

        public String getTxt_nickname() {
            return txt_nickname;
        }

        public void setTxt_nickname(String txt_nickname) {
            this.txt_nickname = txt_nickname;
        }

        public String getTxt_followtime() {
            return txt_followtime;
        }

        public void setTxt_followtime(String txt_followtime) {
            this.txt_followtime = txt_followtime;
        }

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

        public String getTxt_followme() {
            return txt_followme;
        }

        public void setTxt_followme(String txt_followme) {
            this.txt_followme = txt_followme;
        }

    }
}
