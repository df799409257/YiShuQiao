package com.yishuqiao.dto;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName WorkDetailsDTO
 * @Description TODO(作品详情)
 * @Date 2017年8月1日 下午2:29:07
 */
public class WorkDetailsDTO {

    private String txt_code;
    private String txt_message;

    private List<WorksInfoDetail> worksInfoDetail;

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

    public List<WorksInfoDetail> getWorksInfoDetail() {
        return worksInfoDetail;
    }

    public void setWorksInfoDetail(List<WorksInfoDetail> worksInfoDetail) {
        this.worksInfoDetail = worksInfoDetail;
    }

    public class WorksInfoDetail {

        private String txt_id; // 艺术家id
        private String txt_work_id; // 作品id
        private String txt_name; // 作品名称
        private String txt_url; // 作品图片
        private String txt_smallurl; // 作品图片
        private String txt_width; // 作品的宽
        private String txt_height; // 作品的高
        private String work_size; // 作品规格
        private String txt_years; // 作品的年代
        private String txt_material; // 作品的材质
        private String txt_descript; // 作品描述
        private String txt_other;

        public String getTxt_smallurl() {
            return txt_smallurl;
        }

        public void setTxt_smallurl(String txt_smallurl) {
            this.txt_smallurl = txt_smallurl;
        }

        public String getWork_size() {
            return work_size;
        }

        public void setWork_size(String work_size) {
            this.work_size = work_size;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_work_id() {
            return txt_work_id;
        }

        public void setTxt_work_id(String txt_work_id) {
            this.txt_work_id = txt_work_id;
        }

        public String getTxt_name() {
            return txt_name;
        }

        public void setTxt_name(String txt_name) {
            this.txt_name = txt_name;
        }

        public String getTxt_url() {
            return txt_url;
        }

        public void setTxt_url(String txt_url) {
            this.txt_url = txt_url;
        }

        public String getTxt_width() {
            return txt_width;
        }

        public void setTxt_width(String txt_width) {
            this.txt_width = txt_width;
        }

        public String getTxt_height() {
            return txt_height;
        }

        public void setTxt_height(String txt_height) {
            this.txt_height = txt_height;
        }

        public String getTxt_years() {
            return txt_years;
        }

        public void setTxt_years(String txt_years) {
            this.txt_years = txt_years;
        }

        public String getTxt_material() {
            return txt_material;
        }

        public void setTxt_material(String txt_material) {
            this.txt_material = txt_material;
        }

        public String getTxt_descript() {
            return txt_descript;
        }

        public void setTxt_descript(String txt_descript) {
            this.txt_descript = txt_descript;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

    }

}
