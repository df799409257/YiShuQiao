package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.adapter.FragmentFourAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CarouselPicDTO;
import com.yishuqiao.dto.YiYuanDTO;
import com.yishuqiao.dto.YiYuanDTO.YiYuanInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.tsz.afinal.http.AjaxParams;

import org.xutils.http.RequestParams;
import org.xutils.x;

public class FragmentYiYuan extends ForResultNestedCompatFragment implements BGARefreshLayoutDelegate {

    private static final int DISMISS = 1001;
    private static final int SHOW = 1002;
    private CustomProgressDialog progressDialog = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    if (progressDialog != null) {
                        progressDialog.setTouchAble((Boolean) msg.obj);
                        progressDialog.show();
                    }
                    break;
                case DISMISS:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private BGARefreshLayout myRefreshLayout;
    private RelativeLayout content;
    private View failedView = null;

    private int PagerNum = 1;// 分页加载

    private String ArtType = null;

    public static final String BUNDLE_TITLE = "yiyuan";
    private GridView gv_gridview;
    private FragmentFourAdapter adapter;
    private List<YiYuanInfor> newsList = new ArrayList<YiYuanInfor>();
    private View rootView;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_yiyuan, null);
            init(rootView);// 控件初始化
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    public void init(View v) {

        Bundle bundle = getArguments();
        ArtType = bundle.getString("type");

        content = (RelativeLayout) v.findViewById(R.id.content);

        myRefreshLayout = (BGARefreshLayout) v.findViewById(R.id.myRefreshLayout);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        myRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        myRefreshLayout.setPullDownRefreshEnable(true);
        myRefreshLayout.setDelegate(this);

        gv_gridview = (GridView) v.findViewById(R.id.gv_gridview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) gv_gridview.getParent()).addView(emptyView, emptyParams);
        gv_gridview.setEmptyView(emptyView);

        adapter = new FragmentFourAdapter(getActivity(), newsList);
        gv_gridview.setAdapter(adapter);
        gv_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), WriterDetailsActivity.class);
                intent.putExtra(WriterDetailsActivity.ITEMID, newsList.get(arg2).getTxt_id());
                startActivity(intent);

            }
        });

        getData();

    }

    public void getData() {
//        if (PagerNum == 2) {
        showLoadingView();
//        }
        RequestParams params = new RequestParams(HttpUrl.ArtList);
        params.addBodyParameter("type", "0");
        params.addBodyParameter("txt_text", ArtType);
        params.addBodyParameter("pagerindex", Conn.PagerSize);
        params.addBodyParameter("pagernum", "" + PagerNum++);
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {


            }


            @Override
            public void onError(Throwable arg0, boolean arg1) {
                dismissLoadingView();
                myRefreshLayout.endLoadingMore();
                addFailedView();
            }


            @Override
            public void onFinished() {

            }


            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub

                if (PagerNum == 1) {
                    newsList.clear();
                }
                MyApplication.MySharedPreferences.saveFragment4(arg0);
                myRefreshLayout.endLoadingMore();
                dismissLoadingView();
                removeFailedView();

                YiYuanDTO newsDTO = new YiYuanDTO();
                List<YiYuanInfor> newinforList = new ArrayList<YiYuanInfor>();
                if (Conn.IsNetWork) {

                    newsDTO.setTxt_code("0");
                    newsDTO.setTxt_message("请求成功");
                    for (int i = 0; i < 15; i++) {
                        YiYuanInfor newinfor = newsDTO.new YiYuanInfor();
                        newinfor.setTxt_name("测试昵称");
                        if (i == 0) {
                            newinfor.setTxt_url(
                                    "http://imgsrc.baidu.com/baike/pic/item/00e93901213fb80ee2cbb17136d12f2eb938947a.jpg");
                        } else {
                            newinfor.setTxt_url("http://img2.imgtn.bdimg.com/it/u=3322771429,3129580571&fm=26&gp=0.jpg");
                        }
                        newinfor.setTxt_id("0");
                        newinforList.add(newinfor);
                    }
                    newsDTO.setYiYuanInforList(newinforList);
                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(arg0, YiYuanDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    newsList.addAll(newsDTO.getYiYuanInforList());
                    adapter.notifyDataSetChanged();

                    if (newsList.size() < Integer.parseInt(Conn.PagerSize)) {
                        myRefreshLayout.closeLoadingMore();
                    } else {
                        myRefreshLayout.openLoadingMore();
                    }

                } else {
                    // Utils.showToast(getActivity(), newsDTO.getTxt_message());

                }
            }
        });


    }


    public static FragmentYiYuan newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        FragmentYiYuan fragment = new FragmentYiYuan();
        fragment.setArguments(bundle);
        return fragment;
    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        PagerNum = 1;
        getData();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                myRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getData();
        myRefreshLayout.closeLoadingMore();
        return true;
    }

    protected void addFailedView() {
        if (failedView == null) {
            failedView = getActivity().getLayoutInflater().inflate(R.layout.net_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.header);
            content.addView(failedView, params);
            failedView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PagerNum = 1;
                    getData();
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

    /**
     * 显示加载视图
     */
    protected void showLoadingView(boolean isTouchAble) {
        Message m = mHandler.obtainMessage(SHOW, isTouchAble);
        mHandler.sendMessage(m);
    }

    protected void showLoadingView() {
        showLoadingView(true);
    }

    /**
     * 关闭加载视图
     */
    protected void dismissLoadingView() {
        mHandler.sendEmptyMessage(DISMISS);
    }

    protected boolean isShowingLoadingView() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }
        super.onResume();
    }
}
