package com.yishuqiao.dto;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName WriterDetailsDTO
 * @Description TODO(作家详情)
 * @Date 2017年7月24日 上午10:16:50
 */
public class WriterDetailsDTO {

    private String txt_code = "";
    private String txt_message = "";
    private List<ArtistInforDetail> artistInforDetail;

    public List<ArtistInforDetail> getArtistInforDetail() {
        return artistInforDetail;
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

    public void setArtistInforDetail(List<ArtistInforDetail> artistInforDetail) {
        this.artistInforDetail = artistInforDetail;
    }

    public static class ArtistInforDetail {

        private String txt_id = ""; // 艺术家id
        private String txt_name = ""; // 艺术家名称
        private String txt_tel = ""; // 艺术家电话
        private String txt_cover = ""; // 封面图片
        private String txt_isfav = "";// 是否收藏 1=是 0=否
        private String txt_isSP = ""; // 是否点赞 1=是 0=否
        private String txt_other = "";

        public String getTxt_isfav() {
            return txt_isfav;
        }

        public void setTxt_isfav(String txt_isfav) {
            this.txt_isfav = txt_isfav;
        }

        public String getTxt_isSP() {
            return txt_isSP;
        }

        public void setTxt_isSP(String txt_isSP) {
            this.txt_isSP = txt_isSP;
        }

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

        public String getTxt_tel() {
            return txt_tel;
        }

        public void setTxt_tel(String txt_tel) {
            this.txt_tel = txt_tel;
        }

        public String getTxt_cover() {
            return txt_cover;
        }

        public void setTxt_cover(String txt_cover) {
            this.txt_cover = txt_cover;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

    }

}
