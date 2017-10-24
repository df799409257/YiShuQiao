package com.yishuqiao.dto;

public class LoginDTO {

    private String txt_code;
    private String txt_message;
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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

    public class UserInfo {

        private String txt_userid;
        private String txt_usertoken;
        private String txt_nickname;// 昵称
        private String txt_user_tel;// 电话
        private String txt_user_sex;// 性别
        private String txt_user_pic;// 头像
        private String txt_txt_regtime;// 注册日期
        private String txt_user_points;// 积分
        private String txt_user_push;// 推送 1是开 0是关

        public String getTxt_nickname() {
            return txt_nickname;
        }

        public void setTxt_nickname(String txt_nickname) {
            this.txt_nickname = txt_nickname;
        }

        public String getTxt_user_tel() {
            return txt_user_tel;
        }

        public void setTxt_user_tel(String txt_user_tel) {
            this.txt_user_tel = txt_user_tel;
        }

        public String getTxt_user_sex() {
            return txt_user_sex;
        }

        public void setTxt_user_sex(String txt_user_sex) {
            this.txt_user_sex = txt_user_sex;
        }

        public String getTxt_user_pic() {
            return txt_user_pic;
        }

        public void setTxt_user_pic(String txt_user_pic) {
            this.txt_user_pic = txt_user_pic;
        }

        public String getTxt_txt_regtime() {
            return txt_txt_regtime;
        }

        public void setTxt_txt_regtime(String txt_txt_regtime) {
            this.txt_txt_regtime = txt_txt_regtime;
        }

        public String getTxt_user_points() {
            return txt_user_points;
        }

        public void setTxt_user_points(String txt_user_points) {
            this.txt_user_points = txt_user_points;
        }

        public String getTxt_user_push() {
            return txt_user_push;
        }

        public void setTxt_user_push(String txt_user_push) {
            this.txt_user_push = txt_user_push;
        }

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

        public String getTxt_usertoken() {
            return txt_usertoken;
        }

        public void setTxt_usertoken(String txt_usertoken) {
            this.txt_usertoken = txt_usertoken;
        }

    }

}
