package com.yishuqiao.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.SendCommentActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.CommentListAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.dto.CommentListDTO.ReviewInforList;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.HtmlUrlDTO;
import com.yishuqiao.dto.HtmlUrlDTO.Detailinfo;
import com.yishuqiao.dto.NewsDTO.NewInfor;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.ShareDTO;
import com.yishuqiao.dto.ShareDTO.ShareList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.ListViewForScrollView;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.tsz.afinal.http.AjaxParams;

public class CommentWebViewActivity extends BaseActivity implements BGARefreshLayoutDelegate {

    private Detailinfo detailinfo = null;

    private NewInfor newInfor = null;

    @Bind(R.id.tv_attention)
    TextView tv_attention;// 关注按钮

    @Bind(R.id.favorite_icon)
    TextView favorite_icon;// 收藏

    @Bind(R.id.praise_icon)
    TextView praise_icon;// 点赞

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    List<String> images=new ArrayList<>();

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

    public static String HEAD_BEAN = "HEAD_BEAN";// 数据信息

    private Map<String, String> params = null;

    public static String ITEM_ID = "ITEM_ID";// 列表条目的id
    public static String ITEM_TYPE = "ITEM_TYPE";// 列表条目的id
    public static String ACTION_TYPE = "ACTION_TYPE";// 作品点赞、收藏、分享(类型资讯=1、艺苑=3)

    private String item_id = "";
    private String item_type = "";
    private String action_type = "";

    private TextView sendComment;
    private WebView webview;
    private TextView tv_title;
    private WebSettings ws = null;
    private String title = null;

    private ListViewForScrollView gridview;

    private CommentListAdapter adapter;

    private List<CommentListDTO.ReviewInforList> commentList = new ArrayList<CommentListDTO.ReviewInforList>();

    private CustomProgressDialog progressDialog = null;

    private String url = null;
    private boolean isfiish = false;

    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private LinearLayout ll_content = null;
    private View failedView = null;

    private boolean isfv = false;
    private boolean islike = false;
    private boolean isattention = false;
    private String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentwebview);

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

        if (getIntent().hasExtra(HEAD_BEAN)) {
            newInfor = (NewInfor) getIntent().getSerializableExtra(HEAD_BEAN);
        }

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

        if (newInfor != null) {
            if ((newInfor.getTxt_isfav()).equals("1")) {
                favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                isfv = true;
            } else {
                favorite_icon.setBackgroundResource(R.drawable.like_selector);
                isfv = false;
            }

            if ((newInfor.getTxt_isSP()).equals("1")) {
                praise_icon.setBackgroundResource(R.drawable.liked);
                islike = true;
            } else {
                praise_icon.setBackgroundResource(R.drawable.praise_selector);
                islike = false;
            }

            if ((newInfor.getTxt_isfollow()).equals("1")) {
                tv_attention.setText("已关注");
                isattention = true;
            } else {
                tv_attention.setText("关注");
                isattention = false;
            }

            userid = newInfor.getTxt_userid();
            CommentWebViewActivity.this.setResult(RESULT_OK);

        }

        content = (RelativeLayout) findViewById(R.id.content);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);

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

        adapter = new CommentListAdapter(this, commentList);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id != -1) {

                    ReviewInforList dto = commentList.get((int) id);
                    Intent it = new Intent(CommentWebViewActivity.this, CommentDetailActivity.class);
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
                if (isfiish) {
                    Intent it = new Intent(CommentWebViewActivity.this, SendCommentActivity.class);
                    it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
                    it.putExtra(SendCommentActivity.CONTENT, "text_content");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "1");
                    params.put("re_type", item_type);
                    params.put("detail_id", item_id);
                    it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
                    startActivityForResult(it, 0);
                } else {
                    Toast.makeText(CommentWebViewActivity.this, "请等待页面加载完成", Toast.LENGTH_SHORT).show();
                }

            }
        });

        showLoadingView(true);
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

        if (newInfor != null) {
            webview.loadUrl(newInfor.getTxt_h5url());
        }

        webview.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");

        webview.setWebChromeClient(new WebChromeClient() {// 监听网页加载

            private void onReceivedError() {
                addFailedView();
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 10) {
                    // 网页加载完成
                    removeFailedView();
                    dismissLoadingView();
                    addImageClickListner();
                    isfiish = true;
                    if (newProgress == 100) {
                        ll_content.setVisibility(View.VISIBLE);
                        scrollView.scrollTo(0, 0);
                    }
                } else {
                    // 加载中

                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置webview的标题
            public void onReceivedTitle(android.webkit.WebView view, String _title) {
                if (title == null || title.isEmpty()) {
                    if (!TextUtils.isEmpty(_title)) {
                        tv_title.setText(_title);
                    }
                }
                super.onReceivedTitle(view, _title);
            }
        });
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                addFailedView();
            }
        });

        if (newInfor == null) {
            getHtmlUrl();
        }

        getCommentList();

    }
    // 注入js函数监听
    private void addImageClickListner() {
        //遍历页面中所有img的节点，因为节点里面的图片的url即objs[i].src，保存所有图片的src.
        //为每个图片设置点击事件，objs[i].onclick
        webview.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "window.imagelistner.readImageUrl(objs[i].src);  " +
                " objs[i].onclick=function()  " +
                " {  " +
                " window.imagelistner.openImage(this.src);  " +
                "  }  " +
                "}" +
                "})()");
    }
    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void readImageUrl(String img) {     //把所有图片的url保存在ArrayList<String>中
            images.add(img);
        }

        @android.webkit.JavascriptInterface  //对于targetSdkVersion>=17的，要加这个声明
        public void openImage(String clickimg)//点击图片所调用到的函数
        {
            int index = 0;
            ArrayList<String> list = addImages();
            list.remove(0);
            for (String url : list)
                if (url.equals(clickimg)) index = list.indexOf(clickimg);//获取点击图片在整个页面图片中的位置

            List<FindDTO.ItemImageUrlListOriginal> findList=new ArrayList<>();
            for (int i=0;i<list.size();i++){
                FindDTO.ItemImageUrlListOriginal dto=new FindDTO.ItemImageUrlListOriginal();
                dto.setTxt_id(""+i);
                dto.setTxt_url(list.get(i));
                dto.setTxt_other("");
                findList.add(dto);
            }

            Intent intent = new Intent();
            intent.setClass(context, ImageDetailsActivity.class);
            intent.putExtra(ImageDetailsActivity.FindItem,(Serializable) findList);
            intent.putExtra(ImageDetailsActivity.PAGER_INDEX, index);
            context.startActivity(intent);
        }
    }

    //去重复
    private ArrayList<String> addImages() {
        ArrayList<String> list = new ArrayList<String>();
        Set set = new HashSet();
        for (String cd:images) {
            if(set.add(cd)){
                list.add(cd);
            }
        }
        return list;
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
            ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
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
                    info.setTxt_userid("0");
                    dataDTO.setDetailinfo(info);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, HtmlUrlDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    CommentWebViewActivity.this.setResult(RESULT_OK);

                    detailinfo = dataDTO.getDetailinfo();
                    if (!TextUtils.isEmpty(detailinfo.getTxt_h5url())) {
                        url = detailinfo.getTxt_h5url();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    } else {
                        Utils.showToast(CommentWebViewActivity.this, "网页链接加载失败");
                        addFailedView();
                    }

                    if ((detailinfo.getTxt_isfav()).equals("1")) {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                        isfv = true;
                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                        isfv = false;
                    }

                    if ((detailinfo.getTxt_isSP()).equals("1")) {
                        islike = true;
                        praise_icon.setBackgroundResource(R.drawable.liked);
                    } else {
                        islike = false;
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                    }

                    if ((detailinfo.getTxt_isfollow()).equals("1")) {
                        tv_attention.setText("已关注");
                        isattention = true;
                    } else {
                        tv_attention.setText("关注");
                        isattention = false;
                    }

                    userid = detailinfo.getTxt_userid();

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTxt_message());
                    addFailedView();
                    dismissLoadingView();
                }
            }

            @Override
            public void start() {
                // progressDialog.setTouchAble(false);
                // progressDialog.show();
                showLoadingView(true);
            }

            @Override
            public void error() {
                Utils.showToast(CommentWebViewActivity.this, "网络连接失败");
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
            ListViewDataUtils.saveTalk(Integer.parseInt(ListViewDataUtils.readTalk()) + 1 + "");
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
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTip());
                    getCommentList();// 刷新数据

                    // CommentWebViewActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(CommentWebViewActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.CommentList);

    }

    // 点击收藏
    @OnClick(R.id.favorite_icon)
    public void sendFavorite(View view) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", action_type);
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

                    int collect = Integer.parseInt(ListViewDataUtils.readCollect());

                    if (isfv) {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                        isfv = false;
                        Utils.showToast(CommentWebViewActivity.this, "取消收藏成功");

                        if (collect > 0) {
                            ListViewDataUtils.saveCollect(collect - 1 + "");
                        }
                        ListViewDataUtils.saveIsfav("0");

                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                        isfv = true;
                        Utils.showToast(CommentWebViewActivity.this, "收藏成功");

                        ListViewDataUtils.saveCollect(collect + 1 + "");
                        ListViewDataUtils.saveIsfav("1");

                    }

                    // CommentWebViewActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTxt_message());
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
        ajaxParams.put("type", action_type);
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

                    Intent intent = new Intent(CommentWebViewActivity.this, ShareActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShareActivity.SHARE_CONTENT, dataDTO.getShareList());
                    intent.putExtras(bundle);

                    startActivity(intent);

                    // CommentWebViewActivity.this.setResult(RESULT_OK);

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(CommentWebViewActivity.this, "网络连接失败");
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

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", action_type);
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

                    if (islike) {
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                        islike = false;
                        Utils.showToast(CommentWebViewActivity.this, "取消点赞成功");

                        if (Integer.parseInt(ListViewDataUtils.readPraise()) > 0) {
                            ListViewDataUtils.savePraise(Integer.parseInt(ListViewDataUtils.readPraise()) - 1 + "");
                        }

                        ListViewDataUtils.saveIsSP("0");

                    } else {
                        praise_icon.setBackgroundResource(R.drawable.liked);
                        islike = true;
                        Utils.showToast(CommentWebViewActivity.this, "点赞成功");

                        ListViewDataUtils.savePraise(Integer.parseInt(ListViewDataUtils.readPraise()) + 1 + "");

                        ListViewDataUtils.saveIsSP("1");

                    }

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dataDTO.getTxt_message());
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
                Utils.showToast(CommentWebViewActivity.this, "网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);
    }

    /**
     * @param view
     * @Description (点击关注)
     */
    @OnClick(R.id.tv_attention)
    public void submitAttention(View view) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pa_user_id", userid);
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

                    if (isattention) {
                        isattention = false;
                        tv_attention.setText("关注");
                        Utils.showToast(CommentWebViewActivity.this, dto.getTip());
                        ListViewDataUtils.saveIsfollow("0");
                    } else {
                        isattention = true;
                        tv_attention.setText("已关注");
                        Utils.showToast(CommentWebViewActivity.this, dto.getTip());
                        ListViewDataUtils.saveIsfollow("1");
                    }

                } else {
                    Utils.showToast(CommentWebViewActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, CommentWebViewActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/follow.php");

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

    public void onFinish(View view) {

        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                } else {
                    finish();
                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

}
