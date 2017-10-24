package com.yishuqiao.dto;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName SplashDTO
 * @Description TODO(广告)
 * @Date 2017年8月15日 下午3:17:57
 */
public class SplashDTO {

    private String txt_code;
    private String txt_message;
    private Splash splash;

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

    public Splash getSplash() {
        return splash;
    }

    public void setSplash(Splash splash) {
        this.splash = splash;
    }

    public class Splash {

        private String txt_type;
        private String txt_id;
        private String txt_pic;

        public String getTxt_type() {
            return txt_type;
        }

        public void setTxt_type(String txt_type) {
            this.txt_type = txt_type;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_pic() {
            return txt_pic;
        }

        public void setTxt_pic(String txt_pic) {
            this.txt_pic = txt_pic;
        }

    }

}
