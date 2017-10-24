package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 上午10:20:16
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyIntegralDTO.java
 * <p>
 * 类说明：我的积分界面
 */
public class MyIntegralDTO {

    private String txt_code;
    private String txt_message;
    private List<DataInfor> dataList;

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

    public List<DataInfor> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataInfor> dataList) {
        this.dataList = dataList;
    }

    public class DataInfor {

        private String txt_id = "";// 消息ID
        private String txt_count = "";// 积分数量
        private String txt_title = "";// 标题
        private String txt_date = "";// 日期

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_count() {
            return txt_count;
        }

        public void setTxt_count(String txt_count) {
            this.txt_count = txt_count;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_date() {
            return txt_date;
        }

        public void setTxt_date(String txt_date) {
            this.txt_date = txt_date;
        }

    }
}
