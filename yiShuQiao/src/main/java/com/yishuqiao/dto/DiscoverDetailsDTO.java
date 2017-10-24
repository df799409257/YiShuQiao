package com.yishuqiao.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：admin 创建时间：2017年7月19日 下午3:44:59 项目名称：YiShuQiao 文件名称：DiscoverDetailsGradeViewAdapter.java 类说明：首页发现详情界面
 */
public class DiscoverDetailsDTO {

    private String txt_code;
    private String txt_message;
    private FindDetail findDetail;

    public FindDetail getFindDetail() {
        return findDetail;
    }

    public void setFindDetail(FindDetail findDetail) {
        this.findDetail = findDetail;
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

    @SuppressWarnings("serial")
    public class FindDetail implements Serializable {

        private String txt_userpic;// 头像
        private String txt_view;// 浏览次数
        private String txt_title;// 标题
        private String txt_nickname;// 用户名
        private String txt_time;// 时间
        private String txt_praise;// 赞数量
        private String txt_share;// 分享次数
        private String txt_content;// 描述内容
        private String txt_userid;// 用户ID
        private String txt_isfollow;// 是否关注，1：是，0：否
        private String txt_isfav;// 是否收藏，1：是，0：否
        private String txt_isSP; // 是否点赞，1：是，0：否
        private String txt_type;// 类型，1=图文，2=视频
        private String txt_voideurl;// 视频播放地址
        private String txt_other;
        private List<ItemImageUrl> txt_imgList;

        public String getTxt_voideurl() {
            return txt_voideurl;
        }

        public void setTxt_voideurl(String txt_voideurl) {
            this.txt_voideurl = txt_voideurl;
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

        public String getTxt_userpic() {
            return txt_userpic;
        }

        public void setTxt_userpic(String txt_userpic) {
            this.txt_userpic = txt_userpic;
        }

        public String getTxt_view() {
            return txt_view;
        }

        public void setTxt_view(String txt_view) {
            this.txt_view = txt_view;
        }

        public String getTxt_title() {
            return txt_title;
        }

        public void setTxt_title(String txt_title) {
            this.txt_title = txt_title;
        }

        public String getTxt_nickname() {
            return txt_nickname;
        }

        public void setTxt_nickname(String txt_nickname) {
            this.txt_nickname = txt_nickname;
        }

        public String getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(String txt_time) {
            this.txt_time = txt_time;
        }

        public String getTxt_praise() {
            return txt_praise;
        }

        public void setTxt_praise(String txt_praise) {
            this.txt_praise = txt_praise;
        }

        public String getTxt_share() {
            return txt_share;
        }

        public void setTxt_share(String txt_share) {
            this.txt_share = txt_share;
        }

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_userid() {
            return txt_userid;
        }

        public void setTxt_userid(String txt_userid) {
            this.txt_userid = txt_userid;
        }

        public String getTxt_isfollow() {
            return txt_isfollow;
        }

        public void setTxt_isfollow(String txt_isfollow) {
            this.txt_isfollow = txt_isfollow;
        }

        public String getTxt_type() {
            return txt_type;
        }

        public void setTxt_type(String txt_type) {
            this.txt_type = txt_type;
        }

        public String getTxt_other() {
            return txt_other;
        }

        public void setTxt_other(String txt_other) {
            this.txt_other = txt_other;
        }

        public List<ItemImageUrl> getTxt_imgList() {
            return txt_imgList;
        }

        public void setTxt_imgList(List<ItemImageUrl> txt_imgList) {
            this.txt_imgList = txt_imgList;
        }

        public class ItemImageUrl implements Serializable {

            private String txt_url;
            private String txt_id;
            private String txt_other;

            public String getTxt_url() {
                return txt_url;
            }

            public void setTxt_url(String txt_url) {
                this.txt_url = txt_url;
            }

            public String getTxt_id() {
                return txt_id;
            }

            public void setTxt_id(String txt_id) {
                this.txt_id = txt_id;
            }

            public String getTxt_other() {
                return txt_other;
            }

            public void setTxt_other(String txt_other) {
                this.txt_other = txt_other;
            }

        }
    }
}
