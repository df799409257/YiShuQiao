package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017-7-6 下午6:03:17 项目名称：YiShuQiao 文件名称：SearchDTO.java 类说明：搜索界面
 */
public class SearchDTO {

    private String txt_code;
    private String txt_message;
    private List<SearchList> searchList;

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

    public List<SearchList> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchList> searchList) {
        this.searchList = searchList;
    }

    public class SearchList {

        private String txt_id = "";
        private String txt_url = "";// 标题
        private String txt_name = "";// 标题
        private String txt_time = "";// 时间

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

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
        }

    }
}
