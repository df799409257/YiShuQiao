package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin 创建时间：2017-7-6 下午2:50:15 项目名称：YiShuQiao 文件名称：FavoriteDTO.java 类说明：我的收藏
 */
public class FavoriteDTO {

    private String txt_code;
    private String txt_message;
    private List<CollectInfoList> collectInfoList;

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

    public List<CollectInfoList> getCollectInfoList() {
        return collectInfoList;
    }

    public void setCollectInfoList(List<CollectInfoList> collectInfoList) {
        this.collectInfoList = collectInfoList;
    }

    public class CollectInfoList {

        private String txt_id;
        private String txt_title;// 作品标题
        private String txt_creattime;// 作品日期
        private String txt_img;// 作品图片地址
        private String txt_url;// 作品h5连接

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_creattime() {
            return txt_creattime;
        }

        public void setTxt_creattime(String txt_creattime) {
            this.txt_creattime = txt_creattime;
        }

        public String getTxt_img() {
            return txt_img;
        }

        public void setTxt_img(String txt_img) {
            this.txt_img = txt_img;
        }

    }
}
