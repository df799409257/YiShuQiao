package com.yishuqiao.activity.mine;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.adapter.MyIntegralAdapter;
import com.yishuqiao.dto.MyIntegralDTO;
import com.yishuqiao.dto.MyIntegralDTO.DataInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-4 下午2:52:51
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyIntegralActivity.java
 * <p>
 * 类说明：我的积分界面
 */
public class MyIntegralActivity extends BaseActivity {

    private TextView title = null;
    private ListView listview = null;

    private MultiStateView mStateView;

    private MyIntegralAdapter adapter;

    private List<MyIntegralDTO.DataInfor> list = new ArrayList<MyIntegralDTO.DataInfor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myintegral);

        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的积分");

        mStateView = (MultiStateView) findViewById(R.id.stateview);

        listview = (ListView) findViewById(R.id.listview);

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view,
                null);
        LinearLayout.LayoutParams emetyParams = new LinearLayout.LayoutParams(
                -1, -1);
        emetyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emetyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyIntegralAdapter(this, list);
        listview.setAdapter(adapter);

        getDataFromService();

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "13");
        ajaxParams.put("token", "7ef56049939b7296240e3e596757dc03");
        ajaxParams.put("userid", "319039");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                list.clear();
                adapter.notifyDataSetChanged();

                mStateView.setViewState(ViewState.CONTENT);
                MyIntegralDTO dataDTO = new MyIntegralDTO();
                List<MyIntegralDTO.DataInfor> dataList = new ArrayList<MyIntegralDTO.DataInfor>();
                if (!Conn.IsNetWork) {
                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");
                    DataInfor datafor = dataDTO.new DataInfor();
                    datafor.setTxt_id("0");
                    datafor.setTxt_count("10");
                    datafor.setTxt_date("2017-7-4");
                    datafor.setTxt_title("签到奖励");
                    dataList.add(datafor);
                    dataDTO.setDataList(dataList);
                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, MyIntegralDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    list.addAll(dataDTO.getDataList());
                    adapter.notifyDataSetChanged();
                } else {
                    Utils.showToast(MyIntegralActivity.this,
                            dataDTO.getTxt_message());
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
        httpNetWork.postHttp(ajaxParams,
                "http://aap.android.libaite.vip/user/userinfo.php");

    }

    public void onFinish(View view) {
        finish();
    }

}
