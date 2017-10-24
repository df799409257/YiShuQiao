package com.yishuqiao.dto;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ArtSortDTO
 * @Description TODO(艺苑分类)
 * @Date 2017年7月24日 上午9:50:48
 */
public class ArtSortDTO {

    private String txt_code;
    private String txt_message;
    private List<ArtInforType> yiyuanInforType;

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

    public List<ArtInforType> getYiyuanInforType() {
        return yiyuanInforType;
    }

    public void setYiyuanInforType(List<ArtInforType> yiyuanInforType) {
        this.yiyuanInforType = yiyuanInforType;
    }

    public class ArtInforType {

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
