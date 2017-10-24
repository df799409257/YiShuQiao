package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017年7月20日 下午2:34:30
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：NewsSortDTO.java
 * <p>
 * 类说明：资讯分类
 */
public class NewsSortDTO {

    private String txt_code;
    private String txt_message;
    private List<NewInforType> newInforType;

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

    public List<NewInforType> getNewInforType() {
        return newInforType;
    }

    public void setNewInforType(List<NewInforType> newInforType) {
        this.newInforType = newInforType;
    }

    public class NewInforType {

        private String txt_id;
        private String txt_name;
        private String txt_other;

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
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

    }

}
