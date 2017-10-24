package com.yishuqiao.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.adapter.MyLookForWorksFragmentAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.MyNewsFragmentDTO;
import com.yishuqiao.dto.MyNewsFragmentDTO.SubmitFindList;
import com.yishuqiao.dto.MyNewsFragmentDTO.SubmitFindList.ImgList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.LoadMoreListView;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.LoadMoreListView.OnLoadMoreListener;
import com.yishuqiao.view.MultiStateView.ViewState;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName MyLookForWorksFragment
 * @Description TODO(我发起的 资讯界面)
 * @Date 2017年7月29日 下午8:21:10
 */
public class MyPublishedNewsFragment extends Fragment implements BGARefreshLayoutDelegate {

    private int clickPosition = 0;// 记录listview点击的位置
    private int listPosition = 0;// 记录listview可见的位置
    private int listTop = 0;// 记录listview点击的item的偏移量

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private CustomProgressDialog progressDialog = null;
    private View rootView;

    private MyLookForWorksFragmentAdapter adapter;
    private List<MyNewsFragmentDTO.SubmitFindList> list = new ArrayList<MyNewsFragmentDTO.SubmitFindList>();
    int pagerindex = 1;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_mylookforworks, null);
            initView(rootView);// 控件初始化
        }
        return rootView;
    }

    @SuppressLint("InflateParams")
    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyLookForWorksFragmentAdapter(getActivity(), list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clickPosition = position;
                getListPosition();

                Intent intent = new Intent(getActivity(), CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, list.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra("txt_title", "资讯详情");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", list.get(position).getTxt_id());
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivityForResult(intent, 1);

            }
        });

        content = (RelativeLayout) view.findViewById(R.id.content);
        swipeRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.swipe_container);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        swipeRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        swipeRefreshLayout.setPullDownRefreshEnable(true);
        swipeRefreshLayout.setDelegate(this);
        getDataFromService();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        super.onResume();
    }

    // 记住listview的位置 保存当前第一个可见的item的索引和偏移量
    private void getListPosition() {

        listPosition = listview.getFirstVisiblePosition();
        View v = listview.getChildAt(0);
        listTop = (v == null) ? 0 : v.getTop();

        ListViewDataUtils.saveCollect(list.get(clickPosition).getTxt_collect());
        ListViewDataUtils.saveShare(list.get(clickPosition).getTxt_share());
        ListViewDataUtils.saveTalk(list.get(clickPosition).getTxt_talk());
        ListViewDataUtils.saveShow(Integer.parseInt(list.get(clickPosition).getTxt_show()) + 1 + "");
        ListViewDataUtils.savePraise(list.get(clickPosition).getTxt_praise());

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pagerindex", "10");
        ajaxParams.put("pagernum", pagerindex + "");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                if (pagerindex == 1) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.endLoadingMore();
                progressDialog.dismiss();
                removeFailedView();

                MyNewsFragmentDTO dto = new MyNewsFragmentDTO();
                List<MyNewsFragmentDTO.SubmitFindList> dataList = new ArrayList<MyNewsFragmentDTO.SubmitFindList>();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    SubmitFindList data = dto.new SubmitFindList();
                    data.setTxt_content("测试作品，测试作品");
                    data.setTxt_praise("100");
                    data.setTxt_share("100");
                    data.setTxt_time("2017-7-4");
                    data.setTxt_id("0");
                    data.setTxt_state("已通过");
                    data.setTxt_talk("100");
                    data.setTxt_type("1");
                    data.setTxt_show("100");
                    dataList.add(data);

                    ImgList imgList = data.new ImgList();
                    for (int i = 0; i < 1; i++) {
                        imgList.setTxt_url(
                                "http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    }

                    dto.setSubmitFindList(dataList);
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, MyNewsFragmentDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    list.addAll(dto.getSubmitFindList());
                    adapter.notifyDataSetChanged();
                } else {
                    if (pagerindex == 1) {
                        pagerindex--;

                    }
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                progressDialog.show();
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                addFailedView();
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.mySubmit);

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        pagerindex = 1;
        getDataFromService();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                swipeRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        pagerindex++;
        getDataFromService();
        swipeRefreshLayout.closeLoadingMore();
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
                    pagerindex = 1;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (1 == requestCode && resultCode == Activity.RESULT_OK) {
            // 刷新数据
            list.get(clickPosition).setTxt_share(ListViewDataUtils.readShare());
            list.get(clickPosition).setTxt_collect(ListViewDataUtils.readCollect());
            list.get(clickPosition).setTxt_show(ListViewDataUtils.readShow());
            list.get(clickPosition).setTxt_talk(ListViewDataUtils.readTalk());
            list.get(clickPosition).setTxt_praise(ListViewDataUtils.readPraise());
            adapter.notifyDataSetChanged();

            // 根据上次保存的index和偏移量恢复上次的位置
            listview.setSelectionFromTop(listPosition, listTop);
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

}
