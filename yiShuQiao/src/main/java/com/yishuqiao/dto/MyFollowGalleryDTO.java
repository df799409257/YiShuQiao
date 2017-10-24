package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 下午3:36:25
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyFollowGalleryDTO.java
 * <p>
 * 类说明：我关注的画廊界面
 */
public class MyFollowGalleryDTO {

    private String txt_code;
    private String txt_message;
    private List<DataInfor> dataList;

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

    public List<DataInfor> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataInfor> dataList) {
        this.dataList = dataList;
    }

    public class DataInfor {

        private String txt_id;
        private String txt_galleryPic = "";// 作品图片地址
        private String txt_galleryName = "";// 作品名称
        private String txt_date = "";// 时间
        private String txt_num = "";// 数量

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_galleryPic() {
            return txt_galleryPic;
        }

        public void setTxt_galleryPic(String txt_galleryPic) {
            this.txt_galleryPic = txt_galleryPic;
        }

        public String getTxt_galleryName() {
            return txt_galleryName;
        }

        public void setTxt_galleryName(String txt_galleryName) {
            this.txt_galleryName = txt_galleryName;
        }

        public String getTxt_date() {
            return txt_date;
        }

        public void setTxt_date(String txt_date) {
            this.txt_date = txt_date;
        }

        public String getTxt_num() {
            return txt_num;
        }

        public void setTxt_num(String txt_num) {
            this.txt_num = txt_num;
        }
    }
}
