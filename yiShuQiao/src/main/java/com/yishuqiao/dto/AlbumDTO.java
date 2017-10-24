package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月14日 下午7:21:25 项目名称：YiShuQiao 文件名称：WorkDTO.java 类说明：作家详情 相册列表
 */
public class AlbumDTO {

    private String txt_code;
    private String txt_message;
    private List<PictureInfoList> pictureInfoList;

    public List<PictureInfoList> getPictureInfoList() {
        return pictureInfoList;
    }

    public void setPictureInfoList(List<PictureInfoList> pictureInfoList) {
        this.pictureInfoList = pictureInfoList;
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

    public class PictureInfoList {

        private String txt_url;
        private String txt_smallurl;
        private String txt_time;
        private String txt_title;
        private String txt_pic_id;
        private String txt_id;
        private String txt_other;

        public String getTxt_smallurl() {
            return txt_smallurl;
        }

        public void setTxt_smallurl(String txt_smallurl) {
            this.txt_smallurl = txt_smallurl;
        }

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_pic_id() {
            return txt_pic_id;
        }

        public void setTxt_pic_id(String txt_pic_id) {
            this.txt_pic_id = txt_pic_id;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

    }

}
