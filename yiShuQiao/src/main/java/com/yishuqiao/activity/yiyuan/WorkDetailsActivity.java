package com.yishuqiao.activity.yiyuan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.ViewPagerActivity;
import com.yishuqiao.dto.ViewPagerDTO;
import com.yishuqiao.dto.WorkDetailsDTO;
import com.yishuqiao.dto.WorkDetailsDTO.WorksInfoDetail;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.MultiStateView;
import com.yishuqiao.view.MultiStateView.ViewState;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName WorkDetailsActivity
 * @Description TODO(作品详情界面)
 * @Date 2017年8月1日 下午1:55:02
 */
public class WorkDetailsActivity extends BaseActivity {

    private List<WorksInfoDetail> workDetails = new ArrayList<WorkDetailsDTO.WorksInfoDetail>();

    private ArrayList<ViewPagerDTO> imgs = null;

    public static String WORK_ID = "WORK_ID";
    public static String WRITER_ID = "WRITER_ID";
    public static String TITLE = "TITLE";

    private String work_id = "";
    private String writer_id = "";

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.coverPic)
    ImageView coverPic;

    @Bind(R.id.workName)
    TextView workName;// 作品名称

    @Bind(R.id.workSize)
    TextView workSize;// 作品规格

    @Bind(R.id.workDate)
    TextView workDate;// 作品年代

    @Bind(R.id.workTexture)
    TextView workTexture;// 作品材质

    @Bind(R.id.workDescribe)
    TextView workDescribe;// 作品描述

    @Bind(R.id.main_stateview)
    MultiStateView main_stateview;

    private DownLoadImage downLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workdetails);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        downLoadImage = new DownLoadImage(this);

        if (getIntent().hasExtra(TITLE)) {
            tv_title.setText(getIntent().getStringExtra(TITLE));
        }

        if (getIntent().hasExtra(WORK_ID)) {
            work_id = getIntent().getStringExtra(WORK_ID);
        }
        if (getIntent().hasExtra(WRITER_ID)) {
            writer_id = getIntent().getStringExtra(WRITER_ID);
        }

        getDataFromService();

    }

    @OnClick(R.id.coverPic)
    public void coverPic(View view) {

        if (workDetails.size() > 0) {
            imgs = new ArrayList<ViewPagerDTO>();
            ViewPagerDTO pagerDTO = new ViewPagerDTO();
            pagerDTO.setPic_url(workDetails.get(0).getTxt_url());
            pagerDTO.setTxt_content("");
            imgs.add(pagerDTO);
            Intent intent = new Intent(this, ViewPagerActivity.class);
            intent.putExtra(ViewPagerActivity.IMGS, (Serializable) imgs);
            intent.putExtra(ViewPagerActivity.INDEX, 0);
            startActivity(intent);
        }

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_text", writer_id);
        ajaxParams.put("work_id", work_id);
        Log.e("writer_id", "writer_id = " + writer_id);
        Log.e("work_id", "work_id = " + work_id);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (Utils.isGson(t)) {

                    main_stateview.setViewState(ViewState.CONTENT);
                    WorkDetailsDTO newsDTO = new WorkDetailsDTO();
                    List<WorksInfoDetail> worksInfoDetail = new ArrayList<WorkDetailsDTO.WorksInfoDetail>();
                    if (Conn.IsNetWork) {

                        newsDTO.setTxt_code("0");
                        newsDTO.setTxt_message("请求成功");

                        for (int i = 0; i < 1; i++) {
                            WorksInfoDetail details = newsDTO.new WorksInfoDetail();
                            details.setTxt_descript("测试作品");
                            details.setTxt_height("200");
                            details.setTxt_id("0");
                            details.setTxt_material("贴纸");
                            details.setTxt_name("测试001");
                            details.setTxt_other("");
                            details.setTxt_url("");
                            details.setTxt_width("100");
                            details.setTxt_work_id("10");
                            details.setTxt_years("石器时代");
                            worksInfoDetail.add(details);
                        }
                        newsDTO.setWorksInfoDetail(worksInfoDetail);

                    } else {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(t, WorkDetailsDTO.class);
                    }

                    if (newsDTO.getTxt_code().equals("0")) {

                        workDetails.addAll(newsDTO.getWorksInfoDetail());

                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_smallurl())) {
                            Picasso.with(WorkDetailsActivity.this).load(workDetails.get(0).getTxt_smallurl())
                                    .error(R.drawable.icon_error).into(coverPic);
                            // downLoadImage.DisplayImage(workDetails.get(0).getTxt_smallurl(), coverPic);
                        }

                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_name())) {
                            tv_title.setText(workDetails.get(0).getTxt_name());
                        }

                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_name())
                                && null != workDetails.get(0).getTxt_name()) {
                            workName.setVisibility(View.VISIBLE);
                            workName.setText("名称 : " + workDetails.get(0).getTxt_name());
                        }
                        if (!TextUtils.isEmpty(workDetails.get(0).getWork_size())
                                && null != workDetails.get(0).getWork_size()) {
                            workSize.setVisibility(View.VISIBLE);
                            workSize.setText("规格 : " + workDetails.get(0).getWork_size());
                        }
                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_years())
                                && null != workDetails.get(0).getTxt_years()) {
                            workDate.setVisibility(View.VISIBLE);
                            workDate.setText("年代 : " + workDetails.get(0).getTxt_years());
                        }
                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_material())
                                && null != workDetails.get(0).getTxt_material()) {
                            workTexture.setVisibility(View.VISIBLE);
                            workTexture.setText("材质 : " + workDetails.get(0).getTxt_material());
                        }
                        if (!TextUtils.isEmpty(workDetails.get(0).getTxt_descript())
                                && null != workDetails.get(0).getTxt_descript()) {
                            workDescribe.setVisibility(View.VISIBLE);
                            workDescribe.setText("描述 : " + workDetails.get(0).getTxt_descript());
                        }

                    } else {
                        Utils.showToast(WorkDetailsActivity.this, newsDTO.getTxt_message());
                    }

                } else {
                    main_stateview.setViewState(ViewState.EMPTY);
                }

            }

            @Override
            public void start() {
                main_stateview.setViewState(ViewState.LOADING);
            }

            @Override
            public void error() {
                main_stateview.setViewState(ViewState.ERROR);
                main_stateview.getView(ViewState.ERROR).findViewById(R.id.iv_error)
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                getDataFromService();
                            }
                        });
            }
        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.ArtDetailWork);

    }

}
