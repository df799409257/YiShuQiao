package com.yishuqiao.dto;

public class WriterCenterDTO {

    private String txt_code;// 结果码
    private String txt_message;// 提示信息
    private UserIndex userIndex;

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

    public UserIndex getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(UserIndex userIndex) {
        this.userIndex = userIndex;
    }

    public class UserIndex {

        private String txt_userid;// 作者id
        private String txt_username;// 作者昵称
        private String txt_userpic;// 作者头像
        private String txt_isfollow;// 是否关注
        private String txt_follow;// 关注量
        private String txt_fans;// 粉丝量

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

        public String getTxt_username() {
            return txt_username;
        }

        public void setTxt_username(String txt_username) {
            this.txt_username = txt_username;
        }

        public String getTxt_userpic() {
            return txt_userpic;
        }

        public void setTxt_userpic(String txt_userpic) {
            this.txt_userpic = txt_userpic;
        }

        public String getTxt_isfollow() {
            return txt_isfollow;
        }

        public void setTxt_isfollow(String txt_isfollow) {
            this.txt_isfollow = txt_isfollow;
        }

        public String getTxt_follow() {
            return txt_follow;
        }

        public void setTxt_follow(String txt_follow) {
            this.txt_follow = txt_follow;
        }

        public String getTxt_fans() {
            return txt_fans;
        }

        public void setTxt_fans(String txt_fans) {
            this.txt_fans = txt_fans;
        }

    }

}
