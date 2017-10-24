package com.yishuqiao.dto;

import java.util.List;

/**
 * 轮播图实体类
 *
 * @author Administrator
 */
public class CarouselPicDTO {

    private String txt_code;
    private String txt_message;
    private List<FindDetail> findDetail;

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

    public List<FindDetail> getFindDetail() {
        return findDetail;
    }

    public void setFindDetail(List<FindDetail> findDetail) {
        this.findDetail = findDetail;
    }

    public class FindDetail {

        private String txt_pic;
        private String txt_title;
        private String txt_id;// 内容ID
        private String txt_h5url;// 当此处不为空时，优先使用此链接
        private String txt_type;// 0为发现；1为资讯；2为视频，3为艺苑

        public String getTxt_h5url() {
            return txt_h5url;
        }

        public void setTxt_h5url(String txt_h5url) {
            this.txt_h5url = txt_h5url;
        }

        public String getTxt_type() {
            return txt_type;
        }

        public void setTxt_type(String txt_type) {
            this.txt_type = txt_type;
        }

        public String getTxt_pic() {
            return txt_pic;
        }

        public void setTxt_pic(String txt_pic) {
            this.txt_pic = txt_pic;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

    }
}
