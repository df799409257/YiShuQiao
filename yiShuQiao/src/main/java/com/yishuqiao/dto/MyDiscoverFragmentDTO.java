package com.yishuqiao.dto;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName MyCommunicationFragmentDTO
 * @Description TODO(我发起的 发现界面)
 * @Date 2017年8月1日 上午9:43:51
 */
public class MyDiscoverFragmentDTO {

    private String txt_code;
    private String txt_message;
    private List<SubmitFindList> submitFindList;

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

    public List<SubmitFindList> getSubmitFindList() {
        return submitFindList;
    }

    public void setSubmitFindList(List<SubmitFindList> submitFindList) {
        this.submitFindList = submitFindList;
    }

    public class SubmitFindList {

        private String txt_id;// 新闻ID
        private String txt_time;// 日期
        private String txt_talk;// 评论数
        private String txt_show;// 浏览次数
        private String txt_share;// 分享次数
        private String txt_praise;// 点赞数量
        private String txt_collect;// 收藏数量
        private String txt_content;// 标题
        private String txt_state;// 状态 已通过 已下线 待审核 未通过
        private String txt_type; // 类别 : 1为图文 2为视频
        private List<ImgList> txt_imglist;// 封面

        public String getTxt_praise() {
            return txt_praise;
        }

        public void setTxt_praise(String txt_praise) {
            this.txt_praise = txt_praise;
        }

        public List<ImgList> getTxt_imglist() {
            return txt_imglist;
        }

        public void setTxt_imglist(List<ImgList> txt_imglist) {
            this.txt_imglist = txt_imglist;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
        }

        public String getTxt_talk() {
            return txt_talk;
        }

        public void setTxt_talk(String txt_talk) {
            this.txt_talk = txt_talk;
        }

        public String getTxt_show() {
            return txt_show;
        }

        public void setTxt_show(String txt_show) {
            this.txt_show = txt_show;
        }

        public String getTxt_share() {
            return txt_share;
        }

        public void setTxt_share(String txt_share) {
            this.txt_share = txt_share;
        }

        public String getTxt_collect() {
            return txt_collect;
        }

        public void setTxt_collect(String txt_collect) {
            this.txt_collect = txt_collect;
        }

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_state() {
            return txt_state;
        }

        public void setTxt_state(String txt_state) {
            this.txt_state = txt_state;
        }

        public String getTxt_type() {
            return txt_type;
        }

        public void setTxt_type(String txt_type) {
            this.txt_type = txt_type;
        }

        public class ImgList {

            private String txt_url;// 封面图片

            public String getTxt_url() {
                return txt_url;
            }

            public void setTxt_url(String txt_url) {
                this.txt_url = txt_url;
            }

        }
    }

}
