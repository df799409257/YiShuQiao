package com.yishuqiao.activity.yiyuan;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.HtmlUrlDTO;
import com.yishuqiao.dto.HtmlUrlDTO.Detailinfo;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ResumeFragment
 * @Description TODO(作平详情 简历界面 h5)
 * @Date 2017年7月27日 下午5:03:42
 */
public class ResumeFragment extends Fragment {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                webview.loadUrl(url);
            }
        }

        ;
    };

    private WebView webview = null;
    private WebSettings ws = null;
    private MultiStateView mStateView = null;

    public String WriterID = "";// 作家ID

    private String url = "";

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resume, null);

        initView(view);

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initView(View view) {

        mStateView = (MultiStateView) view.findViewById(R.id.main_stateview);
        webview = (WebView) view.findViewById(R.id.webview);

        webview.setVerticalScrollBarEnabled(false);
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

        // webview.loadUrl("http://artist.rbz1672.com/phone/artistinfo/285");

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

        });
        webview.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mStateView.setViewState(ViewState.ERROR);
                mStateView.getView(ViewState.ERROR).findViewById(R.id.iv_error) //
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                webview.loadUrl(url);
                            }
                        });

            }
        });

        Bundle bundle = getArguments();// 从activity传过来的Bundle
        if (bundle != null) {
            WriterID = bundle.getString("WriterID");
        }

        getDataFromService();
    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_id", WriterID);
        if (MyApplication.MySharedPreferences.readIsLogin()) {
            ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
            ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        }
        Log.e("参数", "readToken = " + MyApplication.MySharedPreferences.readToken() + "    txt_userid = "
                + MyApplication.MySharedPreferences.readUserid() + "    WriterID = " + WriterID);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (Utils.isGson(t)) {

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
                        if (!TextUtils.isEmpty(dataDTO.getDetailinfo().getTxt_h5url())) {
                            url = dataDTO.getDetailinfo().getTxt_h5url();
                            Message msg = Message.obtain();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        } else {
                            Utils.showToast(getActivity(), "网页链接加载失败");
                            mStateView.setViewState(ViewState.EMPTY);
                        }
                    } else {
                        // Utils.showToast(getActivity(), dataDTO.getTxt_message());
                        mStateView.setViewState(ViewState.EMPTY);
                    }
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
                mStateView.setViewState(ViewState.EMPTY);
            }
        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Html5);

    }

}
