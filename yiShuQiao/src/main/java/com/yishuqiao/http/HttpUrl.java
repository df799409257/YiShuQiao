package com.yishuqiao.http;

public class HttpUrl {

    /*
     * 测试接口网址
     */
//    public static String Urlmy = "http://ysq.api.hymaker.net/";

    /*
     * 正式接口网址
     */
    public static String Urlmy = "http://101.200.155.163/";

    /*
     * 强制升级、意见反馈
     */
    public static String UpDate = Urlmy + "public/update.php";

    /*
     * 广告页
     */
    public static String AD = Urlmy + "public/update.php?type=2";

    /*
     * 发现列表
     */
    public static String FindList = Urlmy + "find/index.php";

    /*
     * 资讯列表、分类
     */
    public static String NewsList = Urlmy + "news/index.php";

    /*
     * 视频列表、分类
     */
    public static String VideoList = Urlmy + "video/index.php";

    /*
     * 艺苑列表 、分类
     */
    public static String ArtList = Urlmy + "artist/index.php";

    /*
     * 艺苑列表详情
     */
    public static String ArtDetail = Urlmy + "artist/detail.php";

    /*
     * 艺苑列表详情-作品
     */
    public static String ArtDetailWork = Urlmy + "artist/works.php";

    /*
     * 艺苑列表详情-简历（H5）
     */
    public static String ArtDetailIntroduct = Urlmy + "artist/introduct.php";

    /*
     * 艺苑列表详情-评论（H5）
     */
    public static String ArtDetailComment = Urlmy + "artist/comment.php";

    /*
     * 艺苑列表详情-相册
     */
    public static String ArtDetailAlbum = Urlmy + "artist/picture.php";

    /*
     * 艺苑列表详情-活动
     */
    public static String ArtDetailActivity = Urlmy + "artist/activity.php";

    /*
     * 评论:评论列表、发表评论、点赞
     */
    public static String CommentList = Urlmy + "user/review.php";

    /*
     * 作品点赞、收藏、收藏列表、分享
     */
    public static String Action = Urlmy + "user/action.php";

    /*
     * 我的关注、我的粉丝、关注、取消关注
     */
    public static String Follow = Urlmy + "user/follow.php";

    /*
     * 获得用户头像、发现图文、视频上传凭证(七牛上传凭证)
     */
    public static String Upload = Urlmy + "upload/upload.php";

    /*
     * 所有h5页面请求
     */
    public static String Html5 = Urlmy + "home/geturl.php";

    /*
     * 我发布的-发现、资讯
     */
    public static String mySubmit = Urlmy + "user/mySubmit.php";

    /*
     * 消息中心-我参与的
     */
    public static String Message = Urlmy + "user/message.php";

    /*
     * 搜索
     */
    public static String Search = Urlmy + "search/index.php";

    /*
     * 推送开关
     */
    public static String ModifyUser = Urlmy + "user/modifyUser.php";

}
