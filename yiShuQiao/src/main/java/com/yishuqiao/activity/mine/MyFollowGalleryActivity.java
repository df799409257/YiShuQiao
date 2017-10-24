package com.yishuqiao.activity.mine;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.adapter.MyFollowGalleryAdapter;
import com.yishuqiao.dto.MyFollowGalleryDTO;
import com.yishuqiao.dto.MyFollowGalleryDTO.DataInfor;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-4 下午3:03:45
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyFollowGalleryActivity.java
 * <p>
 * 类说明：我关注的画廊界面
 */
public class MyFollowGalleryActivity extends BaseActivity {

    private TextView title = null;
    private ListView listview = null;

    private MultiStateView mStateView;

    private List<MyFollowGalleryDTO.DataInfor> list = new ArrayList<MyFollowGalleryDTO.DataInfor>();
    private MyFollowGalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfollowgrery);

        initView();
    }

    @SuppressLint("InflateParams")
    private void initView() {

        mStateView = (MultiStateView) findViewById(R.id.stateview);

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我关注的画廊");

        listview = (ListView) findViewById(R.id.listview);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view,
                null);
        LinearLayout.LayoutParams emetyParams = new LinearLayout.LayoutParams(
                -1, -1);
        emetyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emetyParams);
        listview.setEmptyView(emptyView);

        adapter = new MyFollowGalleryAdapter(this, list);
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
                MyFollowGalleryDTO dto = new MyFollowGalleryDTO();
                List<MyFollowGalleryDTO.DataInfor> dataList = new ArrayList<MyFollowGalleryDTO.DataInfor>();
                if (!Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");
                    DataInfor datafor = dto.new DataInfor();
                    datafor.setTxt_id("0");
                    datafor.setTxt_num("2");
                    datafor.setTxt_galleryName("测试作品");
                    datafor.setTxt_galleryPic("http://img.zcool.cn/community/01cee257f9eb87a84a0e282b086b70.jpg@900w_1l_2o_100sh.jpg");
                    datafor.setTxt_date("2017-7-4");
                    dataList.add(datafor);
                    dto.setDataList(dataList);
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, MyFollowGalleryDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    list.addAll(dto.getDataList());
                    adapter.notifyDataSetChanged();
                } else {
                    Utils.showToast(MyFollowGalleryActivity.this,
                            dto.getTxt_message());
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
