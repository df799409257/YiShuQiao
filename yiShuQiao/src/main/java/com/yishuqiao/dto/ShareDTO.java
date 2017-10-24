package com.yishuqiao.dto;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ShareDTO
 * @Description TODO(分享)
 * @Date 2017年7月26日 下午1:25:32
 */
@SuppressWarnings("serial")
public class ShareDTO implements Serializable {

    private String txt_code;
    private String txt_message;

    private ShareList shareList;

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

    public ShareList getShareList() {
        return shareList;
    }

    public void setShareList(ShareList shareList) {
        this.shareList = shareList;
    }

    public class ShareList implements Serializable {

        private String txt_id = "";
        private String txt_title = "";// 标题
        private String txt_img = "";// 图标
        private String txt_url = "";// 链接
        private String txt_detail = "";// 内容

        public String getTxt_detail() {
            return txt_detail;
        }

        public void setTxt_detail(String txt_detail) {
            this.txt_detail = txt_detail;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_img() {
            return txt_img;
        }

        public void setTxt_img(String txt_img) {
            this.txt_img = txt_img;
        }

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

    }

}
