package com.yishuqiao.activity.yiyuan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.yishuqiao.R;
import com.yishuqiao.adapter.WorkAdapter;
import com.yishuqiao.dto.WorkDTO;
import com.yishuqiao.dto.WorkDTO.WorksInforList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.MyGridView;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName WorkFragment
 * @Description TODO(作家详情 作品列表)
 * @Date 2017年8月1日 下午2:20:57
 */
public class WorkFragment extends Fragment implements BGARefreshLayoutDelegate {

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

    private View failedView = null;
    private int PagerNum = 1;// 分页加载

    @Bind(R.id.gridview)
    MyGridView gridview;

    @Bind(R.id.myRefreshLayout)
    BGARefreshLayout myRefreshLayout;

    @Bind(R.id.content)
    RelativeLayout content;

    private List<WorkDTO.WorksInforList> list = new ArrayList<WorkDTO.WorksInforList>();
    private WorkAdapter adapter = null;

    private String WriterID = "";// 作家ID

    private RequestQueue mQueue = null;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_work, null);

        ButterKnife.bind(this, view);

        initView(view);

        return view;
    }

    private void initView(View view) {

        progressDialog = new CustomProgressDialog(getActivity());

        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        myRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        myRefreshLayout.setPullDownRefreshEnable(false);
        myRefreshLayout.setDelegate(this);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) gridview.getParent()).addView(emptyView, emptyParams);
        gridview.setEmptyView(emptyView);

        mQueue = getRequestQueue();
        adapter = new WorkAdapter(getActivity(), list, mQueue);
        gridview.setAdapter(adapter);
        // 滑动过程中停止去加载图片
        gridview.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(getActivity(), WorkDetailsActivity.class);
                intent.putExtra(WorkDetailsActivity.WORK_ID, list.get(position).getTxt_workid());
                intent.putExtra(WorkDetailsActivity.TITLE, list.get(position).getTxt_name());
                intent.putExtra(WorkDetailsActivity.WRITER_ID, WriterID);
                startActivity(intent);

            }
        });

    }

    public void getWriterID() {

        WriterID = ((WriterDetailsActivity) getActivity()).getWriterID();

        getDataFromService();

    }

    private void getDataFromService() {

        if (WriterID.equals(""))
            return;

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_text", WriterID);
        ajaxParams.put("pagerindex", Conn.PagerSize);
        ajaxParams.put("pagernum", "" + PagerNum++);
        Log.e("WriterID", "WriterID = " + WriterID);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                myRefreshLayout.endLoadingMore();
                dismissLoadingView();
                removeFailedView();

                if (Utils.isGson(t)) {

                    if (PagerNum == 2) {
                        list.clear();
                    }

                    WorkDTO newsDTO = new WorkDTO();
                    List<WorkDTO.WorksInforList> newinforList = new ArrayList<WorkDTO.WorksInforList>();
                    if (Conn.IsNetWork) {
                        for (int i = 0; i < 16; i++) {
                            newsDTO.setTxt_code("0");
                            newsDTO.setTxt_message("请求成功");
                            WorksInforList newinfor = newsDTO.new WorksInforList();
                            if (i == 0) {
                                newinfor.setTxt_url(
                                        "http://img4.imgtn.bdimg.com/it/u=136860858,557894626&fm=26&gp=0.jpg");
                            } else {
                                newinfor.setTxt_url(
                                        "http://img3.redocn.com/tupian/20151019/xiantiaoshouhuiyishuzuopin_5120030.jpg");
                            }
                            newinfor.setTxt_name("作品" + i);
                            newinfor.setTxt_id("0");
                            newinforList.add(newinfor);
                        }
                        newsDTO.setWorksInforList(newinforList);
                    } else {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(t, WorkDTO.class);
                    }

                    if (newsDTO.getTxt_code().equals("0")) {
                        list.addAll(newsDTO.getWorksInforList());
                        adapter.notifyDataSetChanged();

                        if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                            myRefreshLayout.closeLoadingMore();
                        } else {
                            myRefreshLayout.openLoadingMore();
                        }

                    } else {
                        // Utils.showToast(getActivity(), newsDTO.getTxt_message());
                    }

                } else {
                    addFailedView();
                }

            }

            @Override
            public void start() {
                showLoadingView();
            }

            @Override
            public void error() {

                dismissLoadingView();
                myRefreshLayout.endLoadingMore();
                addFailedView();

            }
        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.ArtDetailWork);

    }

    private RequestQueue getRequestQueue() {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        File cacheDir = new File(getActivity().getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        mQueue.start();

        // clear all volley caches.
        mQueue.add(new ClearCacheRequest(cache, null));
        return mQueue;
    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        PagerNum = 1;
        getDataFromService();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                myRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getDataFromService();
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
                    getDataFromService();
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
     *
     * @param isTouchAble true:可点击 false:不可点击 默认是可点击
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
