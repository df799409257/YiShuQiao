package com.yishuqiao.dto;

import java.util.List;

public class VideoDTO {

    String txt_code;
    String txt_message;
    List<ViewInfor> ViewInforList;

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

    public List<ViewInfor> getViewInforList() {
        return ViewInforList;
    }

    public void setViewInforList(List<ViewInfor> viewInforList) {
        ViewInforList = viewInforList;
    }

    public class ViewInfor {

        private String txt_id;
        private String txt_content;
        private String txt_show;// 浏览次数
        private String txt_duration;// 时长
        private String txt_talk;// 讨论数量
        private String txt_praise;// 赞数量
        private String txt_other;
        private List<Imglist> txt_imglist;

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_show() {
            return txt_show;
        }

        public void setTxt_show(String txt_show) {
            this.txt_show = txt_show;
        }

        public String getTxt_duration() {
            return txt_duration;
        }

        public void setTxt_duration(String txt_duration) {
            this.txt_duration = txt_duration;
        }

        public String getTxt_talk() {
            return txt_talk;
        }

        public void setTxt_talk(String txt_talk) {
            this.txt_talk = txt_talk;
        }

        public String getTxt_praise() {
            return txt_praise;
        }

        public void setTxt_praise(String txt_praise) {
            this.txt_praise = txt_praise;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

        public List<Imglist> getTxt_imglist() {
            return txt_imglist;
        }

        public void setTxt_imglist(List<Imglist> txt_imglist) {
            this.txt_imglist = txt_imglist;
        }


    }
}
