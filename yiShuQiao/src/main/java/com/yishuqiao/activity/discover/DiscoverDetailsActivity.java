package com.yishuqiao.activity.discover;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.CommentDetailActivity;
import com.yishuqiao.activity.HomeActivity;
import com.yishuqiao.activity.ImageDetailsActivity;
import com.yishuqiao.activity.ShareActivity;
import com.yishuqiao.adapter.DiscoverDetailsGradeViewAdapter;
import com.yishuqiao.adapter.DiscoverDetailsListViewAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.dto.CommentListDTO.ReviewInforList;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.ShareDTO;
import com.yishuqiao.dto.ShareDTO.ShareList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.MyToast;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.ListViewForScrollView;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MyGridView;
import com.yishuqiao.view.RoundImageView;
import com.yishuqiao.view.YuanJiaoImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName DiscoverDetailsActivity
 * @Description TODO(首页发现详情界面)
 * @Date 2017年7月25日 上午10:14:52
 */
public class DiscoverDetailsActivity extends BaseActivity implements UniversalVideoView.VideoViewCallback {

    public static String DISCOVER_DETAILS_ID = "item_id";// 发现列表条目的id
    public static String DISCOVER_DETAILS_TITLE = "title";// 发现详情的标题
    public static String DISCOVER_DETAILS_IMA = "ima";// 发现详情的标题
    public static String HEAD_BEAN = "HEAD_BEAN";

    private FindItem findItem = null;

    private String item_id = "";

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.stateview)
    MultiStateView mStateView;

    @Bind(R.id.listview)
    ListViewForScrollView listview;

    @Bind(R.id.gv_gridview)
    MyGridView gv_gridview;

    @Bind(R.id.userIcon)
    RoundImageView userIcon;// 用户头像

    @Bind(R.id.tv_talknum)
    TextView tv_talknum;// 评论数

    @Bind(R.id.tv_praise)
    TextView tv_praise;// 点赞数

    @Bind(R.id.tv_browsenum)
    TextView tv_browsenum;// 浏览数

    @Bind(R.id.userName)
    TextView userName;// 用户名

    @Bind(R.id.date)
    TextView date;// 日期

    @Bind(R.id.tv_guanzhu)
    TextView tv_guanzhu;// 关注按钮

    @Bind(R.id.re_video)
    RelativeLayout re_video;


    @Bind(R.id.chat_content)
    TextView chat_content;// 描述内容

    @Bind(R.id.favorite_icon)
    TextView favorite_icon;// 收藏标志

    @Bind(R.id.share_icon)
    TextView share_icon;// 分享标志

    @Bind(R.id.in_include)
    LinearLayout in_include;

    @Bind(R.id.praise_icon)
    TextView praise_icon;// 点赞标志

    @Bind(R.id.textView1)
    TextView textView1;// 点赞标志


    @Bind(R.id.im_pic1)
    YuanJiaoImageView im_pic1;// 单张图片

    @Bind(R.id.im_pic2)
    YuanJiaoImageView im_pic2;// 单张图片

    @Bind(R.id.rl_gridviewLayout)
    RelativeLayout rl_gridviewLayout;// 图文展示


    @Bind(R.id.rl_pic)
    RelativeLayout rl_pic;// 单张图片展示

    @Bind(R.id.rl_gridview)
    RelativeLayout rl_gridview;// gridview展示

    @Bind(R.id.ln_toolBar)
    LinearLayout ln_toolBar;

    @Bind(R.id.ln_stude)
    LinearLayout ln_stude;

    @Bind(R.id.ln_down1)
    LinearLayout ln_down1;

    @Bind(R.id.ln_down2)
    LinearLayout ln_down2;

    @Bind(R.id.re_top)
    RelativeLayout re_top;


    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;

    int mSeekPosition;
    int cachedHeight;
    boolean isFullscreen;

    private static final String TAG = "MainActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    View mBottomLayout;
    View mVideoLayout;

    private int praiseNum = 0;// 点赞数量

    private int talkNum = 0;// 评论数量

    private DiscoverDetailsGradeViewAdapter gradeViewAdapter;
    private List<FindDTO.FindItemImageUrl> imageUrlList = new ArrayList<FindDTO.FindItemImageUrl>();
    private List<FindDTO.ItemImageUrlListOriginal> imageUrlListOriginal = new ArrayList<FindDTO.ItemImageUrlListOriginal>();
    private DiscoverDetailsListViewAdapter listViewAdapter;
    private List<CommentListDTO.ReviewInforList> commentList = new ArrayList<CommentListDTO.ReviewInforList>();

    private MyToast myToast = null;

    private CustomProgressDialog progressDialog = null;

    private DownLoadImage downLoadImage;

    @Override
    protected void onDestroy() {
//        JCVideoPlayer.releaseAllVideos();
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
        super.onResume();
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
        mSeekPosition = mVideoView.getCurrentPosition();
//        JCVideoPlayer.releaseAllVideos();

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_discoverdetails);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        // statusBarHeight = getStatusBarHeight();

        ButterKnife.bind(this);
        myToast = new MyToast(this);

        initView();

    }

    @SuppressLint("InflateParams")
    private void initView() {

        downLoadImage = new DownLoadImage(this);

        mVideoLayout = findViewById(R.id.video_layout);
        mBottomLayout = findViewById(R.id.bottom_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoViewCallback(this);

        if (getIntent().hasExtra(DISCOVER_DETAILS_ID)) {
            item_id = getIntent().getStringExtra(DISCOVER_DETAILS_ID);
        }

        if (getIntent().hasExtra(DISCOVER_DETAILS_TITLE)) {
            tv_title.setText(getIntent().getStringExtra(DISCOVER_DETAILS_TITLE) + "的作品");
        }

        if (getIntent().hasExtra(HEAD_BEAN)) {
            findItem = (FindItem) getIntent().getSerializableExtra(HEAD_BEAN);
        }

        gradeViewAdapter = new DiscoverDetailsGradeViewAdapter(this, imageUrlList);
        gv_gridview.setAdapter(gradeViewAdapter);

        gv_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(DiscoverDetailsActivity.this, ImageDetailsActivity.class);
                intent.putExtra(ImageDetailsActivity.FindItem, (Serializable) imageUrlListOriginal);
                intent.putExtra(ImageDetailsActivity.PAGER_INDEX, position);
                startActivity(intent);
            }
        });

        View emptyView = LayoutInflater.from(this).inflate(R.layout.discoverdetails_emetyview, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        listViewAdapter = new DiscoverDetailsListViewAdapter(this, commentList);
        listview.setAdapter(listViewAdapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (id != -1) {
                    ReviewInforList dto = commentList.get((int) id);
                    Intent it = new Intent(DiscoverDetailsActivity.this, CommentDetailActivity.class);
                    it.putExtra(CommentDetailActivity.ITEM_ID, item_id);
                    it.putExtra(CommentDetailActivity.ITEM_TYPE, "1");
                    it.putExtra(CommentDetailActivity.COMMENT_ID, commentList.get(position).getTxt_id());
                    it.putExtra(CommentDetailActivity.HEADERBEAN, dto);
                    startActivityForResult(it, 0);
                }

            }
        });


        getDataFormService();

    }

    private void getDataFormService() {

        DiscoverDetailsActivity.this.setResult(RESULT_OK);

        // 获取评论列表
        getCommentList();

        if (findItem != null) {

            if (!TextUtils.isEmpty(findItem.getTxt_url())) {
                Picasso.with(DiscoverDetailsActivity.this).load(findItem.getTxt_url()).fit()
                        .error(R.drawable.default_user_icon).into(userIcon);
            }

            userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DiscoverDetailsActivity.this, WriterPersonalCenterActivity.class);
                    intent.putExtra(WriterPersonalCenterActivity.USERID, findItem.getTxt_userid());
                    intent.putExtra(WriterPersonalCenterActivity.TITLE, findItem.getTxt_name());
                    startActivity(intent);
                }
            });

            userName.setText(findItem.getTxt_name());
            date.setText(findItem.getTxt_time());
            chat_content.setText(findItem.getTxt_content());

            tv_talknum.setText(findItem.getTxt_talk());
            if (!(TextUtils.isEmpty(findItem.getTxt_talk()))) {
                talkNum = Integer.parseInt(findItem.getTxt_talk());
            }
            tv_praise.setText(findItem.getTxt_praise());
            if (!(TextUtils.isEmpty(findItem.getTxt_praise()))) {
                praiseNum = Integer.parseInt(findItem.getTxt_praise());
            }
            tv_browsenum.setText(findItem.getTxt_show());

            if (findItem.getTxt_isfollow().equals("0")) {
                tv_guanzhu.setText("关注");
                tv_guanzhu.setBackgroundResource(R.drawable.corners_red_bg);
            } else if (findItem.getTxt_isfollow().equals("1")) {
                tv_guanzhu.setText("已关注");
                tv_guanzhu.setBackgroundResource(R.drawable.corners_gray_bg);
            }

            if ((findItem.getTxt_isfav()).equals("1")) {
                favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
            } else {
                favorite_icon.setBackgroundResource(R.drawable.like_selector);
            }

            if ((findItem.getTxt_isSP()).equals("1")) {
                praise_icon.setBackgroundResource(R.drawable.liked);
            } else {
                praise_icon.setBackgroundResource(R.drawable.praise_selector);
            }

            if ((findItem.getTxt_type()).equals("1")) {

                rl_gridviewLayout.setVisibility(View.VISIBLE);
                mVideoLayout.setVisibility(View.GONE);

                if (findItem.getTxt_imglist().size() == 1) {
                    rl_pic.setVisibility(View.VISIBLE);
                    rl_gridview.setVisibility(View.GONE);

                    im_pic1.setVisibility(View.VISIBLE);
                    im_pic2.setVisibility(View.GONE);

                    if (!(TextUtils.isEmpty(findItem.getTxt_imglist().get(0).getTxt_url()))) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(0).getTxt_url(), im_pic1);
                        im_pic1.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(DiscoverDetailsActivity.this, ImageDetailsActivity.class);
                                intent.putExtra(ImageDetailsActivity.FindItem,
                                        (Serializable) (findItem.getItemImageUrlListOriginal()));
                                intent.putExtra(ImageDetailsActivity.PAGER_INDEX, 0);
                                startActivity(intent);
                            }
                        });

                    }
                } else if (findItem.getTxt_imglist().size() == 2) {
                    rl_pic.setVisibility(View.VISIBLE);
                    rl_gridview.setVisibility(View.GONE);

                    im_pic1.setVisibility(View.VISIBLE);
                    im_pic2.setVisibility(View.VISIBLE);

                    if (!(TextUtils.isEmpty(findItem.getTxt_imglist().get(0).getTxt_url()))) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(0).getTxt_url(), im_pic1);
                        im_pic1.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(DiscoverDetailsActivity.this, ImageDetailsActivity.class);
                                intent.putExtra(ImageDetailsActivity.FindItem,
                                        (Serializable) (findItem.getItemImageUrlListOriginal()));
                                intent.putExtra(ImageDetailsActivity.PAGER_INDEX, 0);
                                startActivity(intent);
                            }
                        });
                    }

                    if (!(TextUtils.isEmpty(findItem.getTxt_imglist().get(1).getTxt_url()))) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(1).getTxt_url(), im_pic2);
                        im_pic2.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(DiscoverDetailsActivity.this, ImageDetailsActivity.class);
                                intent.putExtra(ImageDetailsActivity.FindItem,
                                        (Serializable) (findItem.getItemImageUrlListOriginal()));
                                intent.putExtra(ImageDetailsActivity.PAGER_INDEX, 1);
                                startActivity(intent);
                            }
                        });
                    }

                } else {
                    rl_pic.setVisibility(View.GONE);
                    rl_gridview.setVisibility(View.VISIBLE);
                    imageUrlList.addAll(findItem.getTxt_imglist());
                    imageUrlListOriginal.addAll(findItem.getItemImageUrlListOriginal());
                    gradeViewAdapter.notifyDataSetChanged();
                }

            } else if ((findItem.getTxt_type()).equals("2")) {
                rl_gridviewLayout.setVisibility(View.GONE);
                mVideoLayout.setVisibility(View.VISIBLE);

                // downLoadImage.DisplayImage( getIntent().getStringExtra(DISCOVER_DETAILS_IMA), icon);

                if (!TextUtils.isEmpty(findItem.getTxt_voideurl())) {
                    if (mSeekPosition > 0) {
                        mVideoView.seekTo(mSeekPosition);
                    } else {
                        setVideoAreaSize(findItem.getTxt_voideurl());
                        mVideoView.start();
                        mMediaController.setTitle(findItem.getTxt_name());
                    }

                }

            }

        }

    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize(final String VIDEO_URL) {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                mVideoView.setVideoPath(VIDEO_URL);
                mVideoView.requestFocus();
            }
        });
    }

    // 获取评论列表
    private void getCommentList() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("re_type", "1");
        ajaxParams.put("detail_id", item_id);
        if (MyApplication.MySharedPreferences.readIsLogin()) {
            ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        }

        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                commentList.clear();

                CommentListDTO dataDTO = new CommentListDTO();
                List<CommentListDTO.ReviewInforList> comment = new ArrayList<CommentListDTO.ReviewInforList>();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    for (int i = 0; i < 3; i++) {
                        ReviewInforList datafor = dataDTO.new ReviewInforList();
                        datafor.setTxt_other("");
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
                    commentList.addAll(dataDTO.getReviewInforList());
                    listViewAdapter.notifyDataSetChanged();

                } else {
                    // myToast.show(dataDTO.getTxt_message());
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

    // 点赞
    @OnClick(R.id.praise_icon)
    public void commitPraise(View view) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
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

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    if ((findItem.getTxt_isSP()).equals("1")) {
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                        findItem.setTxt_isSP("0");
                        myToast.show("取消点赞成功");

                        if (praiseNum > 0) {
                            tv_praise.setText(+praiseNum - 1 + "");
                            praiseNum--;
                        }

                    } else {
                        praise_icon.setBackgroundResource(R.drawable.liked);
                        findItem.setTxt_isSP("1");
                        myToast.show("点赞成功");

                        tv_praise.setText(praiseNum + 1 + "");
                        praiseNum++;

                    }

                    ListViewDataUtils.savePraise(tv_praise.getText().toString());

                } else {
                    Utils.showToast(DiscoverDetailsActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(DiscoverDetailsActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 点击收藏
    @OnClick(R.id.favorite_icon)
    public void sendFavorite(View view) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", item_id);
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                PublicDTO dataDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    // DiscoverDetailsActivity.this.setResult(RESULT_OK);

                    // myToast.show(dataDTO.getTxt_message());

                    int collect = Integer.parseInt(ListViewDataUtils.readCollect());

                    if ((findItem.getTxt_isfav()).equals("1")) {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                        findItem.setTxt_isfav("0");
                        myToast.show("取消收藏成功");

                        if (collect > 0) {
                            ListViewDataUtils.saveCollect(collect - 1 + "");
                        }

                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                        findItem.setTxt_isfav("1");
                        myToast.show("收藏成功");

                        ListViewDataUtils.saveCollect(collect + 1 + "");

                    }

                    ListViewDataUtils.saveIsfav(findItem.getTxt_isfav());

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

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 点击分享
    @OnClick(R.id.share_icon)
    public void sendShare(View view) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
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

                    Intent intent = new Intent(DiscoverDetailsActivity.this, ShareActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShareActivity.SHARE_CONTENT, dataDTO.getShareList());
                    intent.putExtras(bundle);

                    startActivity(intent);

                    // DiscoverDetailsActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(DiscoverDetailsActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(DiscoverDetailsActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 发表评论
    @OnClick(R.id.sendComment)
    public void sendComment(TextView view) {

        Intent it = new Intent(this, SendCommentActivity.class);
        it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
        it.putExtra(SendCommentActivity.CONTENT, "text_content");
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("re_type", "1");
        params.put("detail_id", item_id);
        it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
        startActivityForResult(it, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && resultCode == RESULT_OK) {
            getCommentList();// 刷新数据
            tv_talknum.setText(talkNum + 1 + "");
            ListViewDataUtils.saveTalk(tv_talknum.getText().toString());
        }
    }

    // 评论列表进入作家中心
    public void toWriterCenter(int position) {
        Intent intent = new Intent(this, WriterPersonalCenterActivity.class);
        intent.putExtra(WriterPersonalCenterActivity.USERID, commentList.get(position).getTxt_userid());
        intent.putExtra(WriterPersonalCenterActivity.TITLE, commentList.get(position).getTxt_username());
        startActivity(intent);
    }

    // 评论列表点赞
    public void commitCommentPraise(int position) {

        if (!Utils.checkAnony(this) || commentList.size() == 0) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pr_re_id", commentList.get(position).getTxt_id());
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
                    myToast.show(dataDTO.getTip());
                    getCommentList();// 刷新数据

                    // DiscoverDetailsActivity.this.setResult(RESULT_OK);

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
                myToast.show("网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    // 关注、取消关注
    @OnClick(R.id.tv_guanzhu)
    public void guanzhu(TextView view) {

        if (!Utils.checkAnony(this) || findItem == null) {
            return;
        }

        if (findItem.getTxt_isfollow().equals("0")) {
            chageAttention(false);
        } else if (findItem.getTxt_isfollow().equals("1")) {
            chageAttention(true);
        }

    }

    /**
     * 关注与被关注
     *
     * @param isFollow
     */
    private void chageAttention(final boolean isFollow) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pa_user_id", findItem.getTxt_userid());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("关注成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    if (isFollow) {
                        findItem.setTxt_isfollow("0");
                        tv_guanzhu.setText("关注");
                        tv_guanzhu.setBackgroundResource(R.drawable.corners_red_bg);
                    } else {
                        findItem.setTxt_isfollow("1");
                        tv_guanzhu.setText("已关注");
                        tv_guanzhu.setBackgroundResource(R.drawable.corners_gray_bg);
                    }

                    // DiscoverDetailsActivity.this.setResult(RESULT_OK);

                    ListViewDataUtils.saveIsfollow(findItem.getTxt_isfollow());

                } else {
                    Utils.showToast(DiscoverDetailsActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, DiscoverDetailsActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/follow.php");

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
                if (this.isFullscreen) {
                    mVideoView.setFullscreen(false);
                } else {
                    finish();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.GONE);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        switchTitleBar(!isFullscreen);
    }

    private void switchTitleBar(boolean show) {


        if (show) {
            in_include.setVisibility(View.VISIBLE);
            ln_stude.setVisibility(View.VISIBLE);
            chat_content.setVisibility(View.VISIBLE);
            re_top.setVisibility(View.VISIBLE);
            ln_down1.setVisibility(View.VISIBLE);
            ln_down2.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 10, 10, 10);
            re_video.setLayoutParams(params);

        } else {
            in_include.setVisibility(View.GONE);
            ln_stude.setVisibility(View.GONE);
            chat_content.setVisibility(View.GONE);
            re_top.setVisibility(View.GONE);
            ln_down1.setVisibility(View.GONE);
            ln_down2.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            params.setMargins(0, 0, 0, 0);
            re_video.setLayoutParams(params);
        }

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }
}
