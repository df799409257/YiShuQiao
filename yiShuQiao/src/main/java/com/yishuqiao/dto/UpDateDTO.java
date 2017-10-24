package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月20日 下午5:34:32 项目名称：YiShuQiao 文件名称：UpDateDTO.java 类说明：检查升级
 */
public class UpDateDTO {

    private String txt_code;
    private String txt_message;
    private UpdateInfo chkUpdateInfo;

    public UpdateInfo getChkUpdateInfo() {
        return chkUpdateInfo;
    }

    public void setChkUpdateInfo(UpdateInfo chkUpdateInfo) {
        this.chkUpdateInfo = chkUpdateInfo;
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

    public static class UpdateInfo {

        private String txt_version;// 版本
        private String txt_isupdate;// 1为升级，0为不需要升级
        private String txt_downurl;// 下载链接
        private String txt_description;// 升级描述

        public String getTxt_isupdate() {
            return txt_isupdate;
        }

        public void setTxt_isupdate(String txt_isupdate) {
            this.txt_isupdate = txt_isupdate;
        }

        public String getTxt_version() {
            return txt_version;
        }

        public void setTxt_version(String txt_version) {
            this.txt_version = txt_version;
        }

        public String getTxt_downurl() {
            return txt_downurl;
        }

        public void setTxt_downurl(String txt_downurl) {
            this.txt_downurl = txt_downurl;
        }

        public String getTxt_description() {
            return txt_description;
        }

        public void setTxt_description(String txt_description) {
            this.txt_description = txt_description;
        }

    }

}
