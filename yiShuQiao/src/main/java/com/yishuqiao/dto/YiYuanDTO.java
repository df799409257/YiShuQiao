package com.yishuqiao.dto;

import java.util.List;

public class YiYuanDTO {

    String txt_code;
    String txt_message;
    List<YiYuanInfor> yiYuanInforList;

    public class YiYuanInfor {

        String txt_id;
        String txt_url;
        String txt_name;

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

        public String getTxt_name() {
            return txt_name;
        }

        public void setTxt_name(String txt_name) {
            this.txt_name = txt_name;
        }

    }

    public List<YiYuanInfor> getYiYuanInforList() {
        return yiYuanInforList;
    }

    public void setYiYuanInforList(List<YiYuanInfor> yiYuanInforList) {
        this.yiYuanInforList = yiYuanInforList;
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

}
