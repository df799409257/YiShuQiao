package com.yishuqiao.dto;

import java.io.Serializable;

import java.util.List;

@SuppressWarnings("serial")
public class NewsDTO implements Serializable {

    private String txt_code;
    private String txt_message;
    private List<NewInfor> newInforList;

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

    public List<NewInfor> getNewInforList() {
        return newInforList;
    }

    public void setNewInforList(List<NewInfor> newInforList) {
        this.newInforList = newInforList;
    }

    public class NewInfor implements Serializable {

        private String txt_id;
        private String txt_userid;// 用户ID
        private String txt_url;
        private String txt_name;
        private String txt_time;
        private String txt_content;
        private String txt_show;// 浏览数量
        private String txt_talk;// 讨论数量
        private String txt_praise;// 赞数量
        private String txt_isfav;// 是否收藏，0未收藏，1已收藏
        private String txt_isfollow;// 是否关注，0未关注，1已关注
        private String txt_isSP;// 是否点赞，0未点赞，1已点赞
        private String txt_h5url; // h5链接地址
        private String txt_other;
        private List<ItemImageUrlList> txt_imglist;

        public String getTxt_h5url() {
            return txt_h5url;
        }

        public void setTxt_h5url(String txt_h5url) {
            this.txt_h5url = txt_h5url;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_name() {
            return txt_name;
        }

        public void setTxt_name(String txt_name) {
            this.txt_name = txt_name;
        }

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
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

        public String getTxt_isfav() {
            return txt_isfav;
        }

        public void setTxt_isfav(String txt_isfav) {
            this.txt_isfav = txt_isfav;
        }

        public String getTxt_isfollow() {
            return txt_isfollow;
        }

        public void setTxt_isfollow(String txt_isfollow) {
            this.txt_isfollow = txt_isfollow;
        }

        public String getTxt_isSP() {
            return txt_isSP;
        }

        public void setTxt_isSP(String txt_isSP) {
            this.txt_isSP = txt_isSP;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

        public List<ItemImageUrlList> getTxt_imglist() {
            return txt_imglist;
        }

        public void setTxt_imglist(List<ItemImageUrlList> txt_imglist) {
            this.txt_imglist = txt_imglist;
        }

        public class ItemImageUrlList implements Serializable {

            private String txt_id;
            private String txt_url;
            private String txt_other;

            public String getTxt_id() {
                return txt_id;
            }

            public void setTxt_id(String txt_id) {
                this.txt_id = txt_id;
            }

            public String getTxt_url() {
                return txt_url;
            }

            public void setTxt_url(String txt_url) {
                this.txt_url = txt_url;
            }

            public String getTxt_other() {
                return txt_other;
            }

            public void setTxt_other(String txt_other) {
                this.txt_other = txt_other;
            }

        }

    }

}
