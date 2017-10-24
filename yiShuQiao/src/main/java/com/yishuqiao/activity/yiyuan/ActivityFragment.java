package com.yishuqiao.activity.yiyuan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.activity.YiyuanWebViewActivity;
import com.yishuqiao.adapter.ActivityAdapter;
import com.yishuqiao.dto.ActivityDTO;
import com.yishuqiao.dto.ActivityDTO.ActivityInfoList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.ListViewForScrollView;
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
 * @ClassName ActivityFragment
 * @Description TODO(作者详情 活动界面)
 * @Date 2017年7月24日 下午2:03:05
 */
public class ActivityFragment extends Fragment implements BGARefreshLayoutDelegate {

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
    ListViewForScrollView gridview;

    @Bind(R.id.myRefreshLayout)
    BGARefreshLayout myRefreshLayout;

    @Bind(R.id.content)
    RelativeLayout content;

    private List<ActivityDTO.ActivityInfoList> list = new ArrayList<ActivityDTO.ActivityInfoList>();
    private ActivityAdapter adapter = null;

    public String WriterID = "";// 作家ID

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity, null);

        ButterKnife.bind(this, view);

        initView(view);

        return view;
    }

    private void initView(View view) {

        progressDialog = new CustomProgressDialog(getActivity());

        Bundle bundle = getArguments();// 从activity传过来的Bundle
        if (bundle != null) {
            WriterID = bundle.getString("WriterID");
        }

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

        adapter = new ActivityAdapter(getActivity(), list);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), YiyuanWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, list.get(position).getTxt_ac_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "5");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "3");
                intent.putExtra("txt_title", "活动详情");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "4");
                params.put("txt_id", list.get(position).getTxt_ac_id());
                intent.putExtra(YiyuanWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivity(intent);
            }
        });

        getDataFromService();

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_text", WriterID);
        Log.e("WriterID = ", WriterID);
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

                    ActivityDTO dto = new ActivityDTO();
                    List<ActivityDTO.ActivityInfoList> newinforList = new ArrayList<ActivityDTO.ActivityInfoList>();
                    if (Conn.IsNetWork) {

                        for (int i = 0; i < 16; i++) {
                            dto.setTxt_code("0");
                            dto.setTxt_message("请求成功");
                            ActivityInfoList newinfor = dto.new ActivityInfoList();
                            if (i == 0) {
                                newinfor.setTxt_url(
                                        "http://attach2.scimg.cn/forum/201501/22/143449um75xo0ssqjqhmsc.jpg");
                            } else {
                                newinfor.setTxt_url(
                                        "http://img4.imgtn.bdimg.com/it/u=2743717603,1918909303&fm=26&gp=0.jpg");
                            }
                            newinfor.setTxt_title("活动" + i);
                            newinfor.setTxt_ac_id("0");
                            newinfor.setTxt_id("0");
                            newinfor.setTxt_other("");
                            newinforList.add(newinfor);
                        }
                        dto.setActivityInfoList(newinforList);
                    } else {
                        Gson gson = new Gson();
                        dto = gson.fromJson(t, ActivityDTO.class);
                    }
                    if (dto.getTxt_code().equals("0")) {
                        list.addAll(dto.getActivityInfoList());
                        adapter.notifyDataSetChanged();

                        if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                            myRefreshLayout.closeLoadingMore();
                        } else {
                            myRefreshLayout.openLoadingMore();
                        }

                    } else {
                        // Utils.showToast(getActivity(), dto.getTxt_message());
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
        httpNetWork.postHttp(ajaxParams, HttpUrl.ArtDetailActivity);

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
     * @param touchAble true:可点击 false:不可点击 默认是可点击
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
