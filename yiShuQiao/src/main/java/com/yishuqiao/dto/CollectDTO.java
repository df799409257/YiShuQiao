package com.yishuqiao.dto;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName CollectDTO
 * @Description TODO(收藏、取消收藏的公共实体类)
 * @Date 2017年8月3日 下午7:14:11
 */
public class CollectDTO {

    private String txt_code;// 结果码
    private String txt_message;// 提示信息
    private String collect;// 提示信息

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
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
