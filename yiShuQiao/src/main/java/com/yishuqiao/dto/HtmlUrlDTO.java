package com.yishuqiao.dto;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName HtmlUrlDTO
 * @Description TODO(获取h5链接)
 * @Date 2017年7月28日 下午3:21:29
 */
public class HtmlUrlDTO {

    private String txt_code;
    private String txt_message;
    private Detailinfo detailinfo;// 信息

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

    public Detailinfo getDetailinfo() {
        return detailinfo;
    }

    public void setDetailinfo(Detailinfo detailinfo) {
        this.detailinfo = detailinfo;
    }

    public class Detailinfo {

        private String txt_isfav;// 是否收藏，1：是，0：否
        private String txt_isSP;// 是否点赞，1：是，0：否
        private String txt_isfollow; // 是否关注，1：是，0：否
        private String txt_h5url; // h5链接地址
        private String txt_userid; // 用户ID

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

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

        public String getTxt_isfollow() {
            return txt_isfollow;
        }

        public void setTxt_isfollow(String txt_isfollow) {
            this.txt_isfollow = txt_isfollow;
        }

        public String getTxt_h5url() {
            return txt_h5url;
        }

        public void setTxt_h5url(String txt_h5url) {
            this.txt_h5url = txt_h5url;
        }

    }

}
