package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月14日 下午7:21:25 项目名称：YiShuQiao 文件名称：WorkDTO.java 类说明：作家作品列表
 */
public class WorkDTO {

    private String txt_code;
    private String txt_message;
    private List<WorksInforList> worksInforList;

    public List<WorksInforList> getWorksInforList() {
        return worksInforList;
    }

    public void setWorksInforList(List<WorksInforList> worksInforList) {
        this.worksInforList = worksInforList;
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

    public class WorksInforList {

        private String txt_url;
        private String txt_workid;
        private String txt_name;
        private String txt_other;
        private String txt_id;

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_workid() {
            return txt_workid;
        }

        public void setTxt_workid(String txt_workid) {
            this.txt_workid = txt_workid;
        }

        public String getTxt_name() {
            return txt_name;
        }

        public void setTxt_name(String txt_name) {
            this.txt_name = txt_name;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

    }

}
