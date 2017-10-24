package com.yishuqiao.activity.mine;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.adapter.MyFansAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.MyFansDTO;
import com.yishuqiao.dto.MyFansDTO.FollowInforList;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.MyToast;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxParams;

/**
 * 作者：admin 创建时间：2017-7-4 下午2:54:47 项目名称：YiShuQiao 文件名称：MyFansActivity.java 类说明：我的粉丝界面
 */
public class MyFansActivity extends BaseActivity implements BGARefreshLayoutDelegate {

    private MyToast myToast = null;

    private TextView title = null;

    private List<MyFansDTO.FollowInforList> list = new ArrayList<MyFansDTO.FollowInforList>();
    private MyFansAdapter adapter;
    int pagerindex = 1;

    private ListView listview = null;
    private View failedView = null;
    private BGARefreshLayout swipeRefreshLayout;
    private RelativeLayout content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfans);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        myToast = new MyToast(this);

        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的粉丝");

        listview = (ListView) findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyFansAdapter(this, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyFansActivity.this, WriterPersonalCenterActivity.class);
                intent.putExtra(WriterPersonalCenterActivity.USERID, list.get(position).getTxt_userid());
                intent.putExtra(WriterPersonalCenterActivity.TITLE, list.get(position).getTxt_nickname());
                startActivity(intent);
            }
        });

        content = (RelativeLayout) findViewById(R.id.content);
        swipeRefreshLayout = (BGARefreshLayout) findViewById(R.id.swipe_container);
        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(this, true);
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
        ajaxParams.put("pagerindex", "10");
        ajaxParams.put("pagernum", pagerindex + "");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                if (pagerindex == 1) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.endLoadingMore();
                dismissLoadingView();
                removeFailedView();

                MyFansDTO dataDTO = new MyFansDTO();
                List<MyFansDTO.FollowInforList> dataList = new ArrayList<MyFansDTO.FollowInforList>();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");
                    FollowInforList datafor = dataDTO.new FollowInforList();
                    for (int i = 0; i < 3; i++) {
                        datafor.setTxt_userid("0");
                        datafor.setTxt_nickname("测试用户");
                        datafor.setTxt_followme("0");
                        datafor.setTxt_imgurl("");
                        datafor.setTxt_followtime("2017-7-4");
                        dataList.add(datafor);
                    }
                    dataDTO.setFollowInforList(dataList);
                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, MyFansDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    list.addAll(dataDTO.getFollowInforList());
                    adapter.notifyDataSetChanged();

                    if (list.size() < Integer.parseInt(Conn.PagerSize)) {
                        swipeRefreshLayout.closeLoadingMore();
                    } else {
                        swipeRefreshLayout.openLoadingMore();
                    }
                } else {
                    // myToast.show(dataDTO.getTxt_message());
                    if (pagerindex != 1) {
                        pagerindex--;
                    }
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                showLoadingView();
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                dismissLoadingView();
                swipeRefreshLayout.endLoadingMore();
                addFailedView();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Follow);

    }

    public void submitAttention(int position) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pa_user_id", list.get(position).getTxt_userid());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                dismissLoadingView();

                PublicDTO dataDTO = new PublicDTO();
                if (!Conn.IsNetWork) {
                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("操作成功");
                    dataDTO.setTip("操作成功");
                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    myToast.show(dataDTO.getTip());

                    getDataFromService();

                } else {
                    myToast.show(dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                showLoadingView();
            }

            @Override
            public void error() {
                dismissLoadingView();
                myToast.show("操作失败");
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Follow);
    }

    public void onFinish(View view) {
        finish();
    }

    // 下拉刷新
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
            failedView = getLayoutInflater().inflate(R.layout.net_error_view, null);
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
