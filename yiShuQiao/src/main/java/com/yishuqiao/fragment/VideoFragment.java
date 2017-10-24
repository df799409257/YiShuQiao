package com.yishuqiao.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.adapter.FavoriteAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.CollectDTO;
import com.yishuqiao.dto.FavoriteDTO;
import com.yishuqiao.dto.FavoriteDTO.CollectInfoList;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.tsz.afinal.http.AjaxParams;

/**
 * 作者：admin 创建时间：2017-7-5 下午3:01:41 项目名称：YiShuQiao 文件名称：VideoFragment.java 类说明：我的收藏 视屏列表界面
 */
public class VideoFragment extends Fragment implements BGARefreshLayoutDelegate {

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;
    private CustomProgressDialog progressDialog = null;
    private View rootView;

    private FavoriteAdapter adapter;
    private List<CollectInfoList> list = new ArrayList<CollectInfoList>();
    int pagerindex = 1;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    cancleFavorite(msg.arg1);
                    break;

                default:
                    break;
            }
        }

    };

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
            rootView = inflater.inflate(R.layout.fragment_video, null);
            initView(rootView);// 控件初始化
        }
        return rootView;

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    @SuppressLint("InflateParams")
    private void initView(View view) {

        listview = (ListView) view.findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new FavoriteAdapter(getActivity(), list, handler);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, list.get(position).getTxt_id());
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, list.get(position).getTxt_title());
                startActivity(intent);
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

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_cate", "2");
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

                FavoriteDTO dto = new FavoriteDTO();
                List<CollectInfoList> dataList = new ArrayList<CollectInfoList>();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    CollectInfoList data = dto.new CollectInfoList();
                    data.setTxt_img(
                            "http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    data.setTxt_title("测试作品，测试作品");
                    data.setTxt_creattime("2017-7-4");
                    data.setTxt_id("0");
                    dataList.add(data);
                    dto.setCollectInfoList(dataList);
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, FavoriteDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    list.addAll(dto.getCollectInfoList());
                    adapter.notifyDataSetChanged();
                    if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                        swipeRefreshLayout.closeLoadingMore();
                    } else {
                        swipeRefreshLayout.openLoadingMore();
                    }
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
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 取消收藏
    private void cancleFavorite(int position) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", list.get(position).getTxt_id());
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                progressDialog.dismiss();
                CollectDTO dataDTO = new CollectDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, CollectDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    getDataFromService();
                    Utils.showToast(getActivity(), dataDTO.getCollect());

                } else {
                    Utils.showToast(getActivity(), dataDTO.getTxt_message());
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
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

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

}
