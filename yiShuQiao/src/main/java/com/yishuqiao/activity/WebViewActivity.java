package com.yishuqiao.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.yishuqiao.R;
import com.yishuqiao.activity.discover.SendCommentActivity;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

public class WebViewActivity extends BaseActivity {

    public static String ITEM_ID = "ITEM_ID";// 列表条目的id

    private String item_id = "";

    private TextView sendComment;
    private WebView webview;
    private TextView tv_title;
    private MultiStateView mStateView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebSettings ws = null;
    private String title = null;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        init();

    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    public void init() {

        tv_title = (TextView) findViewById(R.id.tv_title);

        sendComment = (TextView) findViewById(R.id.sendComment);// 发表评论
        sendComment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent it = new Intent(WebViewActivity.this, SendCommentActivity.class);
                it.putExtra(SendCommentActivity.URL, HttpUrl.CommentList);
                it.putExtra(SendCommentActivity.CONTENT, "text_content");
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("re_type", "1");
                params.put("detail_id", item_id);
                it.putExtra(SendCommentActivity.PARAMS, (Serializable) params);
                startActivityForResult(it, 0);

            }
        });

        if (getIntent().hasExtra(ITEM_ID)) {
            item_id = getIntent().getStringExtra(ITEM_ID);
        }
        if (getIntent().hasExtra("txt_title")) {
            title = getIntent().getStringExtra("txt_title");
            tv_title.setText(title);
        }
        if (getIntent().hasExtra("txt_url")) {
            url = getIntent().getStringExtra("txt_url");
        } else {
            url = "http://www.baidu.com";
        }
        webview = (WebView) findViewById(R.id.wb);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        mStateView = (MultiStateView) findViewById(R.id.main_stateview);

        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setAllowFileAccess(true);
        ws = webview.getSettings();
        ws.setAllowFileAccess(true);// 设置允许访问文件数据
        ws.setJavaScriptEnabled(true);// 设置支持javascript脚本
        ws.setBuiltInZoomControls(false);// 设置支持缩放
        ws.setSupportZoom(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        webview.setInitialScale(100);
        webview.loadUrl(url);
        webview.setWebChromeClient(new WebChromeClient() {// 监听网页加载

            @SuppressWarnings("unused")
            private void onReceivedError() {

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mStateView.setViewState(ViewState.CONTENT);
                } else {
                    // 加载中
                    mStateView.setViewState(ViewState.LOADING);
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
                mStateView.setViewState(ViewState.ERROR);
                mStateView.getView(ViewState.ERROR).findViewById(R.id.iv_error) //
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                webview.loadUrl(getIntent().getStringExtra("txt_url"));
                            }
                        });

            }
        });
    }

    public void onFinish(View v) {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
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
}
