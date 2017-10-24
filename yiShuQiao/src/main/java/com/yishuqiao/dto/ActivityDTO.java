package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月17日 下午3:58:41 项目名称：YiShuQiao 文件名称：CommentDTO.java 类说明：作者简介 活动界面
 */
public class ActivityDTO {

    private String txt_code;
    private String txt_message;
    private List<ActivityInfoList> activityInfoList;

    public List<ActivityInfoList> getActivityInfoList() {
        return activityInfoList;
    }

    public void setActivityInfoList(List<ActivityInfoList> activityInfoList) {
        this.activityInfoList = activityInfoList;
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

    public class ActivityInfoList {

        private String txt_title;
        private String txt_url;
        private String txt_ac_id;
        private String txt_id;
        private String txt_other;

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_ac_id() {
            return txt_ac_id;
        }

        public void setTxt_ac_id(String txt_ac_id) {
            this.txt_ac_id = txt_ac_id;
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
