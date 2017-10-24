package com.yishuqiao.dto;

import java.util.List;

public class UserJoinMsgDTO {

    private String txt_code;
    private String txt_message;
    private List<NewsInfor> myInfoList;

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

    public List<NewsInfor> getMyInfoList() {
        return myInfoList;
    }

    public void setMyInfoList(List<NewsInfor> myInfoList) {
        this.myInfoList = myInfoList;
    }

    public class NewsInfor {

        private String txt_id;// 内容ID
        private String txt_userpic;// 用户头像
        private String txt_content;// 评论内容
        private String txt_toptitle;// 头像旁边的标题
        private String txt_title;// 内容标题
        private String txt_type;// 1=发现 2=资讯 3=视频 4=艺苑评论 5=艺苑活动
        private String txt_detailpic;// 内容封面
        private String txt_time;// 时间

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_userpic() {
            return txt_userpic;
        }

        public void setTxt_userpic(String txt_userpic) {
            this.txt_userpic = txt_userpic;
        }

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_toptitle() {
            return txt_toptitle;
        }

        public void setTxt_toptitle(String txt_toptitle) {
            this.txt_toptitle = txt_toptitle;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_type() {
            return txt_type;
        }

        public void setTxt_type(String txt_type) {
            this.txt_type = txt_type;
        }

        public String getTxt_detailpic() {
            return txt_detailpic;
        }

        public void setTxt_detailpic(String txt_detailpic) {
            this.txt_detailpic = txt_detailpic;
        }

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
        }

    }

}
