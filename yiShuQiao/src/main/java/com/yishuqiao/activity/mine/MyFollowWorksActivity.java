package com.yishuqiao.activity.mine;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.adapter.MyFollowWorksAdapter;
import com.yishuqiao.dto.MyFollowWorksDTO;
import com.yishuqiao.dto.MyFollowWorksDTO.DataInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName MyFollowWorksActivity
 * @Description TODO(我关注的作品界面)
 * @Date 2017年9月5日 下午6:28:28
 */
public class MyFollowWorksActivity extends BaseActivity {

    private TextView title = null;
    private ListView listview = null;

    private MultiStateView mStateView;

    private List<MyFollowWorksDTO.DataInfor> list = new ArrayList<MyFollowWorksDTO.DataInfor>();
    private MyFollowWorksAdapter adapter;
    private int pagerindex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfollowwork);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {

        mStateView = (MultiStateView) findViewById(R.id.stateview);

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我关注的作品");

        listview = (ListView) findViewById(R.id.listview);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emetyParams = new LinearLayout.LayoutParams(-1, -1);
        emetyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emetyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyFollowWorksAdapter(this, list);
        listview.setAdapter(adapter);

        getDataFromService();

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "13");
        ajaxParams.put("token", "7ef56049939b7296240e3e596757dc03");
        ajaxParams.put("userid", "319039");
        ajaxParams.put("pagerindex", "10");
        ajaxParams.put("pagernum", pagerindex + "");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                if (pagerindex == 1) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }

                mStateView.setViewState(ViewState.CONTENT);
                MyFollowWorksDTO dataDTO = new MyFollowWorksDTO();
                List<MyFollowWorksDTO.DataInfor> dataList = new ArrayList<MyFollowWorksDTO.DataInfor>();
                if (!Conn.IsNetWork) {
                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");
                    DataInfor datafor = dataDTO.new DataInfor();
                    datafor.setTxt_id("0");
                    datafor.setTxt_worksName("测试作品");
                    datafor.setTxt_worksPic(
                            "http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    datafor.setTxt_date("2017-7-4");
                    dataList.add(datafor);
                    dataDTO.setDataList(dataList);
                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, MyFollowWorksDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    list.addAll(dataDTO.getDataList());
                    adapter.notifyDataSetChanged();
                } else {
                    if (pagerindex == 1) {
                        mStateView.setViewState(ViewState.EMPTY);
                        mStateView.getView(ViewState.EMPTY).findViewById(R.id.iv_error) //
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        getDataFromService();
                                    }
                                });
                    }
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
                mStateView.setViewState(ViewState.LOADING);
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
                mStateView.setViewState(ViewState.ERROR);
                mStateView.getView(ViewState.ERROR).findViewById(R.id.iv_error) //
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                getDataFromService();
                            }
                        });
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, "http://aap.android.libaite.vip/user/userinfo.php");

    }

    public void onFinish(View view) {
        finish();
    }

}
