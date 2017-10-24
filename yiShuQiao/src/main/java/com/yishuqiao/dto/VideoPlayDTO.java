package com.yishuqiao.dto;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName VideoPlayDTO
 * @Description TODO(视屏播放)
 * @Date 2017年7月24日 下午4:03:06
 */
public class VideoPlayDTO {

    private String txt_code;
    private String txt_message;
    private VideoPlayInfo videoDetail;

    public VideoPlayInfo getVideoDetail() {
        return videoDetail;
    }

    public void setVideoDetail(VideoPlayInfo videoDetail) {
        this.videoDetail = videoDetail;
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

    public class VideoPlayInfo {

        private String txt_content;// 标题
        private String txt_id;
        private String txt_show;// 播放次数
        private String txt_praise;// 点赞数量
        private String txt_description;// 描述
        private String txt_duration;// 视频时长
        private String txt_videourl;// 视频地址
        private String txt_collect;// 收藏次数
        private String txt_isfav;// 是否收藏 1=是 0=否
        private String txt_isSP;// 是否点赞 1=是 0=否
        private String txt_talk;// 评论数
        private String txt_video_hd_url;
        private List<Imglist> txt_imglist;// 封面地址

        public String getTxt_content() {
            return txt_content;
        }

        public void setTxt_content(String txt_content) {
            this.txt_content = txt_content;
        }

        public String getTxt_id() {
            return txt_id;
        }

        public void setTxt_id(String txt_id) {
            this.txt_id = txt_id;
        }

        public String getTxt_show() {
            return txt_show;
        }

        public void setTxt_show(String txt_show) {
            this.txt_show = txt_show;
        }

        public String getTxt_praise() {
            return txt_praise;
        }

        public void setTxt_praise(String txt_praise) {
            this.txt_praise = txt_praise;
        }

        public String getTxt_description() {
            return txt_description;
        }

        public void setTxt_description(String txt_description) {
            this.txt_description = txt_description;
        }

        public String getTxt_duration() {
            return txt_duration;
        }

        public void setTxt_duration(String txt_duration) {
            this.txt_duration = txt_duration;
        }

        public String getTxt_videourl() {
            return txt_videourl;
        }

        public void setTxt_videourl(String txt_videourl) {
            this.txt_videourl = txt_videourl;
        }

        public String getTxt_collect() {
            return txt_collect;
        }

        public void setTxt_collect(String txt_collect) {
            this.txt_collect = txt_collect;
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

        public String getTxt_talk() {
            return txt_talk;
        }

        public void setTxt_talk(String txt_talk) {
            this.txt_talk = txt_talk;
        }

        public List<Imglist> getTxt_imglist() {
            return txt_imglist;
        }

        public void setTxt_imglist(List<Imglist> txt_imglist) {
            this.txt_imglist = txt_imglist;
        }

        public String getTxt_video_hd_url() {
            return txt_video_hd_url;
        }

        public void setTxt_video_hd_url(String txt_video_hd_url) {
            this.txt_video_hd_url = txt_video_hd_url;
        }
    }

}
