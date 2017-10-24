package com.yishuqiao.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ViewPagerDTO implements Serializable {

    private String pic_url;
    private String txt_content;
    private String pager_id;

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getTxt_content() {
        return txt_content;
    }

    public void setTxt_content(String txt_content) {
        this.txt_content = txt_content;
    }

    public String getPager_id() {
        return pager_id;
    }

    public void setPager_id(String pager_id) {
        this.pager_id = pager_id;
    }

}
