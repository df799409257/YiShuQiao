package com.yishuqiao.dto;

import java.util.List;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 下午3:18:13
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyFollowWorksDTO.java
 * <p>
 * 类说明：我关注的作品界面
 */
public class MyFollowWorksDTO {

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
        private String txt_worksPic = "";// 作品图片地址
        private String txt_worksName = "";// 作品名称
        private String txt_date = "";// 时间

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_worksPic() {
            return txt_worksPic;
        }

        public void setTxt_worksPic(String txt_worksPic) {
            this.txt_worksPic = txt_worksPic;
        }

        public String getTxt_worksName() {
            return txt_worksName;
        }

        public void setTxt_worksName(String txt_worksName) {
            this.txt_worksName = txt_worksName;
        }

        public String getTxt_date() {
            return txt_date;
        }

        public void setTxt_date(String txt_date) {
            this.txt_date = txt_date;
        }

    }

}
