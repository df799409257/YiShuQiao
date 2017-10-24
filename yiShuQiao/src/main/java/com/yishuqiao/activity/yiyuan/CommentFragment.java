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
import com.yishuqiao.adapter.CommentAdapter;
import com.yishuqiao.dto.CommentDTO;
import com.yishuqiao.dto.CommentDTO.CommmentInfoList;
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
 * 作者：admin 创建时间：2017年7月17日 上午10:52:05 项目名称：YiShuQiao 文件名称：CommentFragment.java 类说明：作家详情 讨论界面
 */
public class CommentFragment extends Fragment implements BGARefreshLayoutDelegate {

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

    private List<CommentDTO.CommmentInfoList> list = new ArrayList<CommentDTO.CommmentInfoList>();
    private CommentAdapter adapter = null;

    public String WriterID = "";// 作家ID

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment, null);

        ButterKnife.bind(this, view);

        initView(view);

        return view;
    }

    private void initView(View view) {

        Bundle bundle = getArguments();// 从activity传过来的Bundle
        if (bundle != null) {
            WriterID = bundle.getString("WriterID");
        }

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

        adapter = new CommentAdapter(getActivity(), list);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), YiyuanWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, list.get(position).getTxt_re_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "4");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "3");
                intent.putExtra("txt_title", "评论详情");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "3");
                params.put("txt_id", list.get(position).getTxt_re_id());
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

                    // TODO Auto-generated method stub
                    CommentDTO dto = new CommentDTO();
                    List<CommentDTO.CommmentInfoList> inforList = new ArrayList<CommentDTO.CommmentInfoList>();
                    if (Conn.IsNetWork) {

                        dto.setTxt_code("0");
                        dto.setTxt_message("请求成功");

                        for (int i = 0; i < 8; i++) {
                            CommmentInfoList infor = dto.new CommmentInfoList();
                            infor.setTxt_title("测试标题" + i);
                            infor.setTxt_discuss("373");
                            infor.setTxt_reads("7178");
                            infor.setTxt_content("测试内容" + i);
                            infor.setTxt_re_id(i + "");
                            infor.setTxt_other("");
                            infor.setTxt_id(i + "");
                            inforList.add(infor);
                        }
                        dto.setCommmentInfoList(inforList);
                    } else {
                        Gson gson = new Gson();
                        dto = gson.fromJson(t, CommentDTO.class);
                    }
                    if (dto.getTxt_code().equals("0")) {
                        list.addAll(dto.getCommmentInfoList());
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
        httpNetWork.postHttp(ajaxParams, HttpUrl.ArtDetailComment);

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
