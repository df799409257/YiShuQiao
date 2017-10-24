package com.yishuqiao.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.SendCommentActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.YiyuanCommentListAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.dto.CommentListDTO.ReviewInforList;
import com.yishuqiao.dto.HtmlUrlDTO;
import com.yishuqiao.dto.HtmlUrlDTO.Detailinfo;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.ListViewForScrollView;
import com.yishuqiao.view.MyGridView;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;

import net.tsz.afinal.http.AjaxParams;

public class YiyuanWebViewActivity extends BaseActivity implements BGARefreshLayoutDelegate {

    private Detailinfo detailinfo;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                webview.loadUrl(url);
            }
        }

        ;
    };

    public static String URL_PARAMS = "URL_PARAMS";// 获取h5ur连接的参数

    private Map<String, String> params = null;

    public static String SHOW_SENDCOMMENT = "SHOW_SENDCOMMENT";// 是否显示发表评论 0显示 1不显示
    public static String ITEM_ID = "ITEM_ID";// 列表条目的id
    public static String ITEM_TYPE = "ITEM_TYPE";// 列表条目的id
    public static String ACTION_TYPE = "ACTION_TYPE";// 作品点赞、收藏、分享(类型资讯=1、艺苑=3)

    private String item_id = "";
    private String item_type = "";
    private String action_type = "";

    private WebView webview;
    private TextView tv_title;
    private WebSettings ws = null;
    private String title = null;

    private ListViewForScrollView gridview;

    private YiyuanCommentListAdapter adapter;

    private List<CommentListDTO.ReviewInforList> commentList = new ArrayList<CommentListDTO.ReviewInforList>();

    private CustomProgressDialog progressDialog = null;

    private String url = null;

    private TextView sendComment;

    boolean isfiish = false;

    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private View failedView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yiyuanwebview);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ButterKnife.bind(this);

        init();

    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings({"deprecation", "unchecked"})
    public void init() {

        tv_title = (TextView) findViewById(R.id.tv_title);

        if (getIntent().hasExtra(ACTION_TYPE)) {
            action_type = getIntent().getStringExtra(ACTION_TYPE);
        }
        if (getIntent().hasExtra(ITEM_ID)) {
            item_id = getIntent().getStringExtra(ITEM_ID);
        }
        if (getIntent().hasExtra(ITEM_TYPE)) {
            item_type = getIntent().getStringExtra(ITEM_TYPE);
        }

        if (getIntent().hasExtra("txt_title")) {
            title = getIntent().getStringExtra("txt_title");
            tv_title.setText(title);
        }

        if (getIntent().hasExtra(URL_PARAMS)) {
            params = (Map<String, String>) getIntent().getSerializableExtra(URL_PARAMS);
        }

        content = (RelativeLayout) findViewById(R.id.content);

        swipeRefreshLayout = (BGARefreshLayout) findViewById(R.id.swipe_container);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(this, true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        swipeRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        swipeRefreshLayout.setPullDownRefreshEnable(false);
        swipeRefreshLayout.setDelegate(this);

        gridview = (ListViewForScrollView) findViewById(R.id.gridview);

        View emptyView = LayoutInflater.from(this).inflate(R.layout.discoverdetails_emetyview, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) gridview.getParent()).addView(emptyView, emptyParams);
        gridview.setEmptyView(emptyView);

        adapter = new YiyuanCommentListAdapter(this, commentList);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id != -1) {

                    ReviewInforList dto = commentList.get((int) id);
                    Intent it = new Intent(YiyuanWebViewActivity.this, CommentDetailActivity.class);
                    it.putExtra(CommentDetailActivity.ITEM_ID, item_id);
                    it.putExtra(CommentDetailActivity.ITEM_TYPE, item_type);
                    it.putExtra(CommentDetailActivity.COMMENT_ID, commentList.get(position).getTxt_id());
                    it.putExtra(CommentDetailActivity.HEADERBEAN, dto);
                    startActivityForResult(it, 0);

                }
            }
        });

        sendComment = (TextView) findViewById(R.id.sendComment);// 发表评论
        sendComment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent it = new Intent(YiyuanWebViewActivity.this, SendCommentActivity.class);
                it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
                it.putExtra(SendCommentActivity.CONTENT, "text_content");
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("re_type", item_type);
                params.put("detail_id", item_id);
                it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
                startActivityForResult(it, 0);

            }
        });

        webview = (WebView) findViewById(R.id.wb);

        /* 指定垂直滚动条是否有叠加样式 */
        webview.setVerticalScrollbarOverlay(true);
        /* 设置滚动条的样式 */
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setAllowFileAccess(true);
        ws = webview.getSettings();
        ws.setAllowFileAccess(true);// 设置允许访问文件数据
        ws.setJavaScriptEnabled(true);// 设置支持javascript脚本
        /* 设置为true表示支持使用js打开新的窗口 */
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setBuiltInZoomControls(false);// 设置支持缩放
        ws.setSupportZoom(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // 设置 缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        ws.setDomStorageEnabled(true);
        /* 提高网页渲染的优先级 */
        ws.setRenderPriority(RenderPriority.HIGH);
        // 把图片加载放在最后来加载渲染
        // ws.setBlockNetworkImage(true);

        // 在同种分辨率的情况下,屏幕密度不一样的情况下,自动适配页面:
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scale = dm.densityDpi;
        if (scale == 240) {
            webview.getSettings().setDefaultZoom(ZoomDensity.FAR);
        } else if (scale == 160) {
            webview.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
        } else {
            webview.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
        }

        // webview.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent ev) {
        //
        // ((WebView) v).requestDisallowInterceptTouchEvent(true);
        //
        // return false;
        // }
        // });

        // webview.setInitialScale(100);//为100%， 代表不缩放。 最小缩放等级为25

        // webview.loadUrl(url);

        webview.setWebChromeClient(new WebChromeClient() {// 监听网页加载

            @SuppressWarnings("unused")
            private void onReceivedError() {

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    dismissLoadingView();
                    content.setVisibility(View.VISIBLE);
                    isfiish = true;
                } else {
                    // 加载中

                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置webview的标题
            public void onReceivedTitle(android.webkit.WebView view, String _title) {

                super.onReceivedTitle(view, _title);
            }
        });
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                addFailedView();
            }
        });

        getHtmlUrl();

        getCommentList();

    }

    private void getHtmlUrl() {

        isfiish = false;

        AjaxParams ajaxParams = new AjaxParams();
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            ajaxParams.put(entry.getKey(), entry.getValue());
        }
        if (MyApplication.MySharedPreferences.readIsLogin()) {
            params.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            params.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        }
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                removeFailedView();

                HtmlUrlDTO dataDTO = new HtmlUrlDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    Detailinfo info = dataDTO.new Detailinfo();
                    info.setTxt_h5url("http://ysq.api.hymaker.net/home/newsdetail.php?txt_artid=1736");
                    info.setTxt_isfav("0");
                    info.setTxt_isfollow("0");
                    info.setTxt_isSP("0");
                    dataDTO.setDetailinfo(info);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, HtmlUrlDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    detailinfo = dataDTO.getDetailinfo();
                    if (!TextUtils.isEmpty(detailinfo.getTxt_h5url())) {
                        url = detailinfo.getTxt_h5url();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } else {
                        Utils.showToast(YiyuanWebViewActivity.this, "网页链接加载失败");
                        addFailedView();
                    }

                } else {
                    Utils.showToast(YiyuanWebViewActivity.this, dataDTO.getTxt_message());
                    addFailedView();
                    dismissLoadingView();
                }
            }

            @Override
            public void start() {
                // progressDialog.setTouchAble(false);
                // progressDialog.show();
                showLoadingView(false);
            }

            @Override
            public void error() {
                Utils.showToast(YiyuanWebViewActivity.this, "网络连接失败");
                dismissLoadingView();
                addFailedView();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Html5);

    }

    @Override
    protected void onResume() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        super.onResume();
    }

    // 获取评论列表
    private void getCommentList() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("re_type", item_type);
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
                    adapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == requestCode && resultCode == RESULT_OK) {
            getCommentList();// 刷新数据
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

        if (!MyApplication.MySharedPreferences.readIsLogin()) {
            Utils.showToast(YiyuanWebViewActivity.this, "请先登录!");
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
                    Utils.showToast(YiyuanWebViewActivity.this, dataDTO.getTip());
                    getCommentList();// 刷新数据
                } else {
                    Utils.showToast(YiyuanWebViewActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(YiyuanWebViewActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
            return true;
        } else {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        getHtmlUrl();

        getCommentList();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                swipeRefreshLayout.endRefreshing();
            }
        }, 1500);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        swipeRefreshLayout.closeLoadingMore();
        return true;
    }

    protected void addFailedView() {
        if (failedView == null) {
            failedView = getLayoutInflater().inflate(R.layout.net_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.header);
            content.addView(failedView, params);
            failedView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getHtmlUrl();
                    getCommentList();
                }
            });
        }

    }

    protected void removeFailedView() {
        if (failedView != null) {
            content.removeView(failedView);
            failedView = null;
        }
    }

}
