package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月17日 下午3:58:41 项目名称：YiShuQiao 文件名称：CommentDTO.java 类说明：作者简介 评论界面
 */
public class CommentDTO {

    private String txt_code;
    private String txt_message;
    private List<CommmentInfoList> commmentInfoList;

    public List<CommmentInfoList> getCommmentInfoList() {
        return commmentInfoList;
    }

    public void setCommmentInfoList(List<CommmentInfoList> commmentInfoList) {
        this.commmentInfoList = commmentInfoList;
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

    public class CommmentInfoList {

        private String txt_title;// 标题
        private String txt_content;// 内容
        private String txt_discuss;// 讨论数量
        private String txt_reads;// 阅读数量
        private String txt_id;// id信息
        private String txt_re_id;
        private String txt_other;

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_discuss() {
            return txt_discuss;
        }

        public void setTxt_discuss(String txt_discuss) {
            this.txt_discuss = txt_discuss;
        }

        public String getTxt_reads() {
            return txt_reads;
        }

        public void setTxt_reads(String txt_reads) {
            this.txt_reads = txt_reads;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_re_id() {
            return txt_re_id;
        }

        public void setTxt_re_id(String txt_re_id) {
            this.txt_re_id = txt_re_id;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

    }

}
