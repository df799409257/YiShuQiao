package com.yishuqiao.activity.video;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.ijkplayer.bean.VideoijkBean;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.CommentDetailActivity;
import com.yishuqiao.activity.HomeActivity;
import com.yishuqiao.activity.ShareActivity;
import com.yishuqiao.activity.discover.SendCommentActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.VideoPlayAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CollectDTO;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.dto.CommentListDTO.ReviewInforList;
import com.yishuqiao.dto.Imglist;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.ShareDTO;
import com.yishuqiao.dto.ShareDTO.ShareList;
import com.yishuqiao.dto.VideoPlayDTO;
import com.yishuqiao.dto.VideoPlayDTO.VideoPlayInfo;
import com.yishuqiao.dto.VideoUrlDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.MyToast;
import com.yishuqiao.utils.NetUtil;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.ListViewForScrollView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName VideoPlayActivity
 * @Description TODO(视屏播放)
 * @Date 2017年7月24日 下午3:20:44
 */
public class VideoPlayActivity extends BaseActivity {

    private String content = "";

    private CustomProgressDialog progressDialog = null;

    public static String VIDEO_PLAY_ID = "item_id";// 列表条目的id
    public static String VIDEO_PLAY_TITLE = "title";// 详情的标题
    public static int VIDEO_PLAY_RESULT = 100;

    private String item_id = "";

    private MyToast myToast = null;

    private List<ReviewInforList> list = new ArrayList<ReviewInforList>();

    private VideoPlayInfo videoPlayInfo = null;

    private VideoPlayAdapter adapter;

    private int praiseNum = 0;// 点赞数量
    private int favoriteNum = 0;// 收藏数量


    private static final String TAG = "MainActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    View mBottomLayout;
    View mVideoLayout;

    PlayerView player=null;


    @Bind(R.id.favorite_icon)
    TextView favorite_icon;// 收藏标志

    @Bind(R.id.praise_icon)
    TextView praise_icon;// 点赞标志

    @Bind(R.id.tv_name)
    TextView tv_name;// 视屏名称

    @Bind(R.id.tv_bofang)
    TextView tv_bofang;// 视屏播放次数

    @Bind(R.id.tv_content)
    TextView tv_content;// 视屏描述信息

    @Bind(R.id.tv_comment)
    TextView tv_comment;// 视屏评论数量

    @Bind(R.id.tv_praise)
    TextView tv_praise;// 视屏点赞数量

    @Bind(R.id.tv_collect)
    TextView tv_collect;// 视屏收藏数量


    @Bind(R.id.bottom_layout)
    LinearLayout bottom_layout;

    @Bind(R.id.lv_list)
    ListViewForScrollView listveiw;// 评论列表


//    @Bind(R.id.header)
//    LinearLayout header;//



    @Bind(R.id.show_more)
    RelativeLayout show_more;// 文字描述展开更多

    @Bind(R.id.scrollView)
    ScrollView scrollView;// 文字描述展开更多

    private int height = 0;

    private String urlpath = "";

    private boolean isTwoTalk = false;

    private DownLoadImage downloadImage = null;

    List<VideoijkBean> BeanList=new ArrayList<>();

    boolean isbanping=false;



    @Override
    protected void onResume() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        if (!isbanping){
            if (player != null) {
                player.onResume();
            }
        }

        isbanping=false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (myToast != null) {
            myToast.cancel();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        if (!isbanping){
            if (player != null) {
                player.onPause();
            }
        }

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_videoplay);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        myToast = new MyToast(this);
        ButterKnife.bind(this);


        boolean netConnect = this.isNetConnect();

        initView();

    }


    private void initView() {
        player = new PlayerView(this);
        mVideoLayout = findViewById(R.id.app_video_box);
        // 获取视频和高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mVideoLayout.measure(w, h);
        height = mVideoLayout.getLayoutParams().height;
        Log.e("height  = ", height + "");


        mBottomLayout = findViewById(R.id.bottom_layout);

        downloadImage = new DownLoadImage(this);

        if (getIntent().hasExtra(VIDEO_PLAY_ID)) {
            item_id = getIntent().getStringExtra(VIDEO_PLAY_ID);
        }

//        if (getIntent().hasExtra(VIDEO_PLAY_TITLE)) {
//            tv_title.setText(getIntent().getStringExtra(VIDEO_PLAY_TITLE));
//        }

        View emptyView = LayoutInflater.from(this).inflate(R.layout.discoverdetails_emetyview, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listveiw.getParent()).addView(emptyView, emptyParams);
        listveiw.setEmptyView(emptyView);

        adapter = new VideoPlayAdapter(this, list);

        listveiw.setAdapter(adapter);

        listveiw.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (id != -1) {
                    isbanping=true;
                    isTwoTalk = true;
                    ReviewInforList dto = list.get((int) id);
                    Intent it = new Intent(VideoPlayActivity.this, CommentDetailActivity.class);
                    it.putExtra(CommentDetailActivity.HEIGHT, height + "");
                    it.putExtra(CommentDetailActivity.ITEM_ID, item_id);
                    it.putExtra(CommentDetailActivity.ITEM_TYPE, "3");
                    it.putExtra(CommentDetailActivity.COMMENT_ID, list.get(position).getTxt_id());
                    it.putExtra(CommentDetailActivity.HEADERBEAN, dto);
                    startActivityForResult(it, 0);


                }

            }
        });


        getDataFromService();

    }

    private void getDataFromService() {

        getCommentList();

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "13");
        ajaxParams.put("txt_videoid", item_id);
        ajaxParams.put("diy", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                Log.e("zsyccc","zsyccc="+t);

                VideoPlayDTO dataDTO = new VideoPlayDTO();

                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    VideoPlayInfo datafor = dataDTO.new VideoPlayInfo();
                    datafor.setTxt_description("测试信息测试信息测试信息");
                    datafor.setTxt_content("测试用户");
                    datafor.setTxt_duration("30");
                    datafor.setTxt_collect("20");
                    datafor.setTxt_show("20");
                    datafor.setTxt_praise("20");
                    datafor.setTxt_talk("1");
                    datafor.setTxt_isfav("1");
                    datafor.setTxt_isSP("0");
                    datafor.setTxt_videourl("http://baobab.wdjcdn.com/145076769089714.mp4");

                    List<Imglist> imglist = new ArrayList<Imglist>();
                    for (int i = 0; i < 1; i++) {
                        Imglist pic = new Imglist();
                        pic.setTxt_url("");
                        imglist.add(pic);
                    }

                    datafor.setTxt_imglist(imglist);

                    dataDTO.setVideoDetail(datafor);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, VideoPlayDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    VideoPlayActivity.this.setResult(RESULT_OK);

                    videoPlayInfo = dataDTO.getVideoDetail();
                    tv_name.setText(videoPlayInfo.getTxt_content());
                    tv_bofang.setText(
                            TextUtils.isEmpty(videoPlayInfo.getTxt_show()) ? "" : videoPlayInfo.getTxt_show() + "次播放");

                    content = videoPlayInfo.getTxt_description();
                    // TextViewUtils.toggleEllipsize(tv_content, content);
                    tv_content.setText(content);

                    tv_content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                        @Override
                        public boolean onPreDraw() {
                            // 这个回调会调用多次，获取完行数记得注销监听
                            tv_content.getViewTreeObserver().removeOnPreDrawListener(this);
                            // Utils.showToast(VideoPlayActivity.this, tv_content.getLineCount() + "");
                            if (tv_content.getLineCount() > 4 && content.length() > 6) {
                                tv_content.setMaxLines(4);
                                tv_content.setEllipsize(TruncateAt.END);
                                show_more.setVisibility(View.VISIBLE);
                                show_more.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        tv_content.setMaxLines(1000);
                                        show_more.setVisibility(View.GONE);
                                    }
                                });

                                // int lineEndIndex = tv_content.getLayout().getLineEnd(2);
                                // String html = tv_content.getText().subSequence(0, lineEndIndex - 8)
                                // + "……<font color='#1E90FF'>[更多内容]</font>";
                                // tv_content.setText(Html.fromHtml(html));

                            }
                            return false;
                        }
                    });

                    tv_comment.setText(videoPlayInfo.getTxt_talk());
                    tv_praise.setText(videoPlayInfo.getTxt_praise());
                    tv_collect.setText(videoPlayInfo.getTxt_collect());

                    if (!TextUtils.isEmpty(videoPlayInfo.getTxt_praise())) {
                        praiseNum = Integer.parseInt(videoPlayInfo.getTxt_praise());
                    }

                    if (!TextUtils.isEmpty(videoPlayInfo.getTxt_collect())) {
                        favoriteNum = Integer.parseInt(videoPlayInfo.getTxt_collect());
                    }

                    if ((videoPlayInfo.getTxt_isfav()).equals("1")) {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                    }

                    if ((videoPlayInfo.getTxt_isSP()).equals("1")) {
                        praise_icon.setBackgroundResource(R.drawable.liked);
                    } else {
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                    }

//                    if (videoPlayInfo.getTxt_video_hd_url().equals("-1")) {
                        VideoijkBean bean=new VideoijkBean();
                        bean.setStream("高清");
                        bean.setUrl(videoPlayInfo.getTxt_video_hd_url());

//                        if (!TextUtils.isEmpty(videoPlayInfo.getTxt_videourl())) {
                            getVideoUrl(videoPlayInfo.getTxt_videourl(),bean);

//                        }
//                    }
//                    else{
//                        String videoUrl = videoPlayInfo.getTxt_video_hd_url();
//                        player.setTitle(videoPlayInfo.getTxt_content());
//                        player.setScaleType(PlayStateParams.fitparent);
//                        player.hideMenu(true);
//                        player.forbidTouch(false);
//                        player.showThumbnail(new OnShowThumbnailListener() {
//                            @Override
//                            public void onShowThumbnail(ImageView ivThumbnail) {
//                                x.image().bind(ivThumbnail,videoPlayInfo.getTxt_imglist().get(0).getTxt_url());
//                            }
//                        });
//                        player.setPlaySource(videoUrl);
//                        player.startPlay();
//                    }


                } else {
                    myToast.show(dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub

            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                Log.e("zsy", "error");
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.VideoList);

    }

    public void getVideoUrl(String url, final VideoijkBean gqbean) {

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.configRequestExecutionRetryCount(3);// 请求错误重试次数
        finalHttp.configTimeout(8000);// 超时时间
        finalHttp.get(url, new AjaxCallBack<Object>() {

            public void onFailure(Throwable t, int errorNo, String strMsg) {
                myToast.show("视频加载失败！");
            }


            public void onStart() {

            }


            public void onSuccess(Object t) {

                String resopnse = t.toString();
                String json = resopnse.substring(resopnse.indexOf("=") + 1, resopnse.indexOf(";"));

                Gson gson = new Gson();
                VideoUrlDTO dto = gson.fromJson(json, VideoUrlDTO.class);

                VideoijkBean bqbean=new VideoijkBean();
                bqbean.setStream("标清");
                bqbean.setUrl(dto.getData().getL());
                BeanList.add(gqbean);
                BeanList.add(bqbean);

//                String videoUrl = dto.getData().getL();
                player.setTitle(videoPlayInfo.getTxt_content());
                player.setScaleType(PlayStateParams.fitparent);
                player.hideMenu(true);
                player.forbidTouch(false);
                player.showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        x.image().bind(ivThumbnail,videoPlayInfo.getTxt_imglist().get(0).getTxt_url());
                    }
                });
                player.setPlaySource(BeanList);
                player.startPlay();
                Log.e("zsy","player.getDuration()="+player.getDuration());
                player.getDuration();

            };
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

    // 获取评论列表
    private void getCommentList() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("re_type", "3");
        ajaxParams.put("detail_id", item_id);
        Log.e("item_id=", item_id);
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());

        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                list.clear();

                CommentListDTO dataDTO = new CommentListDTO();
                List<ReviewInforList> comment = new ArrayList<ReviewInforList>();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    for (int i = 0; i < 3; i++) {
                        ReviewInforList datafor = dataDTO.new ReviewInforList();
                        datafor.setTxt_other("other");
                        datafor.setTxt_id(i + "");
                        datafor.setTxt_url("");
                        datafor.setTxt_content("测试评论信息");
                        datafor.setTxt_ispraise("0");
                        datafor.setTxt_username("测试用户");
                        datafor.setTxt_praise("20");
                        datafor.setTxt_recount("3");
                        datafor.setTxt_time("7-13");
                        datafor.setTxt_userid("");
                        comment.add(datafor);
                    }
                    dataDTO.setReviewInforList(comment);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, CommentListDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    list.addAll(dataDTO.getReviewInforList());
                    adapter.notifyDataSetChanged();
                    tv_comment.setText(list.size() + "");
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);
    }

    // 点击发表评论
    @OnClick(R.id.sendComment)
    public void sendComment(View view) {
        isTwoTalk = false;
        Intent it = new Intent(this, SendCommentActivity.class);
        it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
        it.putExtra(SendCommentActivity.CONTENT, "text_content");
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("re_type", "3");
        params.put("detail_id", item_id);
        it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
        startActivityForResult(it, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && resultCode == RESULT_OK) {
            getCommentList();// 刷新数据
            ListViewDataUtils.saveTalk(Integer.parseInt(ListViewDataUtils.readTalk()) + 1 + "");
        }
    }

    // 点击收藏
    @OnClick(R.id.favorite_icon)
    public void sendFavorite(View view) {

        if (!Utils.checkAnony(this) || videoPlayInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", item_id);
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    myToast.show(dataDTO.getTxt_message());

                    if ((videoPlayInfo.getTxt_isfav()).equals("1")) {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                        videoPlayInfo.setTxt_isfav("0");
                        myToast.show("取消收藏成功");

                        if (favoriteNum > 0) {
                            tv_collect.setText((favoriteNum - 1) + "");
                            favoriteNum = favoriteNum - 1;
                        }

                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                        videoPlayInfo.setTxt_isfav("1");
                        myToast.show("收藏成功");

                        if (favoriteNum >= 0) {
                            tv_collect.setText((favoriteNum + 1) + "");
                            favoriteNum = favoriteNum + 1;
                        }

                    }

                    // VideoPlayActivity.this.setResult(RESULT_OK);

                } else {
                    myToast.show(dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(VideoPlayActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 点击分享
    @OnClick(R.id.share_icon)
    public void sendShare(View view) {

        if (!Utils.checkAnony(this) || videoPlayInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", item_id);
        ajaxParams.put("txt_cate", "3");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                ShareDTO dataDTO = new ShareDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    ShareList share = dataDTO.new ShareList();
                    share.setTxt_id("1799");
                    share.setTxt_img("http://img.yishuqiao.cn/news/img/1470315032794085002.png");
                    share.setTxt_title("测试标题1");
                    share.setTxt_url("http://www.baidu.com");

                    dataDTO.setShareList(share);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, ShareDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    // Utils.showToast(DiscoverDetailsActivity.this, dataDTO.getTxt_message());

                    Intent intent = new Intent(VideoPlayActivity.this, ShareActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShareActivity.SHARE_CONTENT, dataDTO.getShareList());
                    intent.putExtras(bundle);

                    startActivity(intent);

                    // VideoPlayActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(VideoPlayActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(VideoPlayActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 点击点赞
    @OnClick(R.id.praise_icon)
    public void sendPraise(View view) {

        if (!Utils.checkAnony(this) || videoPlayInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", item_id);
        ajaxParams.put("txt_cate", "0");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                CollectDTO dataDTO = new CollectDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, CollectDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    if ((videoPlayInfo.getTxt_isSP()).equals("1")) {
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                        videoPlayInfo.setTxt_isSP("0");

                        if (praiseNum > 0) {
                            tv_praise.setText((praiseNum - 1) + "");
                            praiseNum = praiseNum - 1;
                        }
                        myToast.show("取消点赞成功");

                    } else {
                        praise_icon.setBackgroundResource(R.drawable.liked);
                        videoPlayInfo.setTxt_isSP("1");

                        if (praiseNum >= 0) {
                            tv_praise.setText((praiseNum + 1) + "");
                            praiseNum = praiseNum + 1;
                        }
                        myToast.show("点赞成功");

                    }

                    // VideoPlayActivity.this.setResult(RESULT_OK);
                    ListViewDataUtils.savePraise(tv_praise.getText().toString());

                } else {
                    Utils.showToast(VideoPlayActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(VideoPlayActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);
    }

    // 评论列表进入作家中心
    public void toWriterCenter(int position) {
        Intent intent = new Intent(this, WriterPersonalCenterActivity.class);
        intent.putExtra(WriterPersonalCenterActivity.USERID, list.get(position).getTxt_userid());
        intent.putExtra(WriterPersonalCenterActivity.TITLE, list.get(position).getTxt_username());
        startActivity(intent);
    }

    // 评论列表点赞
    public void commitPraise(int position) {

        if (!MyApplication.MySharedPreferences.readIsLogin()) {
            Toast.makeText(VideoPlayActivity.this, "请先登录", Toast.LENGTH_LONG).show();
            return;
        }

        if (videoPlayInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pr_re_id", list.get(position).getTxt_id());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    Utils.showToast(VideoPlayActivity.this, dataDTO.getTxt_message());
                    getCommentList();// 刷新数据

                    // VideoPlayActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(VideoPlayActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                Utils.showToast(VideoPlayActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }


    public void onFinish(View view) {

        if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//判断是否为竖屏
                    finish();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
            return true;
        }
        // else {
        // if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
        // startActivity(new Intent(this, HomeActivity.class));
        // finish();
        // } else {
        // finish();
        // }
        // }

        return super.onKeyDown(keyCode, event);

    }
    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        if((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }else {
            return false;
        }
    }
}
