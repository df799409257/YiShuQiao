package com.yishuqiao.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName CommentListDTO
 * @Description TODO(评论列表)
 * @Date 2017年7月25日 上午9:47:47
 */
@SuppressWarnings("serial")
public class CommentListDTO implements Serializable {

    private String txt_code;
    private String txt_message;

    private List<ReviewInforList> reviewInforList;

    public List<ReviewInforList> getReviewInforList() {
        return reviewInforList;
    }

    public void setReviewInforList(List<ReviewInforList> reviewInforList) {
        this.reviewInforList = reviewInforList;
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

    public class ReviewInforList implements Serializable {

        private String txt_id;// 讨论id
        private String txt_userid;// 用户id
        private String txt_ispraise;// 是否已赞，0：否，1：是
        private String txt_praise;// 点赞，数据为0时，会返回一个‘赞’字
        private String txt_url;// 用户头像
        private String txt_username;// 用户昵称，如果是作者本人评论，会附加[作者]
        private String txt_time;// 评论时间，1天以内的评论，会返回N小时前, 以及评论人地区
        private String txt_content;// 描述内容
        private String txt_recount;// 回复数
        private String txt_other;

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

        public String getTxt_ispraise() {
            return txt_ispraise;
        }

        public void setTxt_ispraise(String txt_ispraise) {
            this.txt_ispraise = txt_ispraise;
        }

        public String getTxt_praise() {
            return txt_praise;
        }

        public void setTxt_praise(String txt_praise) {
            this.txt_praise = txt_praise;
        }

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_username() {
            return txt_username;
        }

        public void setTxt_username(String txt_username) {
            this.txt_username = txt_username;
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

        public String getTxt_recount() {
            return txt_recount;
        }

        public void setTxt_recount(String txt_recount) {
            this.txt_recount = txt_recount;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

    }

}
