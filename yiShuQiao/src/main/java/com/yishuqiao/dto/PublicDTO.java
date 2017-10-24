package com.yishuqiao.dto;

/**
 * 作者：admin 创建时间：2017-7-7 下午7:18:05 项目名称：YiShuQiao 文件名称：PublicDTO.java 类说明：网络请求的公共实体类
 */
public class PublicDTO {

    private String txt_code;// 结果码
    private String txt_message;// 提示信息
    private String tip;// 提示信息

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
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

}
