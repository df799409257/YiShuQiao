package com.yishuqiao.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import net.tsz.afinal.http.AjaxParams;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.adapter.GuidAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;

public class SplashActivity extends BaseActivity {

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();
    public static String ID = "id";
    public static String TYPE = "type";
    private String id = null;
    private String type = null;

    private ViewPager viewPager;
    private List<View> viewList = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /* set it to be full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        init();
        addViewPage();
    }

    public void init() {

        if (getIntent().hasExtra(ID)) {
            id = getIntent().getStringExtra(ID);
        }
        if (getIntent().hasExtra(TYPE)) {
            type = getIntent().getStringExtra(TYPE);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);

    }

    @SuppressLint("InflateParams")
    public void addViewPage() {
        LayoutInflater inflater = LayoutInflater.from(SplashActivity.this);
        View view_1 = inflater.inflate(R.layout.vp_1, null);
        View view_2 = inflater.inflate(R.layout.vp_2, null);
        View view_3 = inflater.inflate(R.layout.vp_3, null);
        View view_4 = inflater.inflate(R.layout.vp_4, null);
        ImageView im_go = (ImageView) view_4.findViewById(R.id.im_go);
        im_go.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                MyApplication.MySharedPreferences.saveIsYinDao(false);
                toNext();
            }

        });
        viewList.add(view_1);
        viewList.add(view_2);
        viewList.add(view_3);
        viewList.add(view_4);
        viewPager.setAdapter(new GuidAdapter(viewList));
    }

    private void toNext() {
        if (id == null || type == null) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        } else {
            // 0为发现；1为资讯；2为视频，3为艺苑
            if (type.equals("0")) {
                getSendDate(id);
            } else if (type.equals("1")) {

                Intent intent = new Intent(this, CommentWebViewActivity.class);
                intent.putExtra(CommentWebViewActivity.ITEM_ID, id);
                intent.putExtra(CommentWebViewActivity.ITEM_TYPE, "2");
                intent.putExtra(CommentWebViewActivity.ACTION_TYPE, "1");
                intent.putExtra("txt_title", "资讯详情");
                intent.putExtra("FLAG", "isAD");

                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "1");
                params.put("txt_id", id);
                intent.putExtra(CommentWebViewActivity.URL_PARAMS, (Serializable) params);

                startActivity(intent);

                finish();

            } else if (type.equals("2")) {

                Intent intent = new Intent(this, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, id);
                intent.putExtra("FLAG", "isAD");
                startActivity(intent);
                finish();

            } else if (type.equals("3")) {

                Intent intent = new Intent(this, WriterDetailsActivity.class);
                intent.putExtra(WriterDetailsActivity.ITEMID, id);
                intent.putExtra("FLAG", "isAD");
                startActivity(intent);
                finish();

            }
        }

    }

    public void getSendDate(final String id) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_findid", id);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                FindDTO newsDTO = new FindDTO();
                List<FindItem> newinforList = new ArrayList<FindDTO.FindItem>();
                if (Conn.IsNetWork) {

                    newsDTO.setTxt_code("0");
                    newsDTO.setTxt_message("请求成功");

                    for (int i = 0; i < 3; i++) {
                        FindItem newinfor = newsDTO.new FindItem();
                        newinfor.setTxt_name("测试昵称");
                        newinfor.setTxt_id(i + "");
                        newinfor.setTxt_url("");
                        newinfor.setTxt_content(
                                "测试作品，测试作品测试作品，测试作品测试作品，测试作品测试作品，测试作品测试作品，测试作品，测试作品测试作品，测试作品测试作品，测试作品，测试作品测试作品，测试作品测试作品，测试作品");
                        newinfor.setTxt_id("0");
                        newinfor.setTxt_praise("0");
                        newinfor.setTxt_talk("0");
                        newinfor.setTxt_time("04-11 10:00");
                        newinfor.setTxt_other("");
                        List<FindItemImageUrl> findItemImageUrlList = new ArrayList<FindDTO.FindItemImageUrl>();
                        for (int j = 0; j < 8; j++) {
                            FindItemImageUrl findItemImageUrl = newsDTO.new FindItemImageUrl();
                            findItemImageUrl.setTxt_other("测试信息");
                            findItemImageUrl
                                    .setTxt_url("http://www.gmw.cn/images/2005-12/18/xin_561202181152562033333.jpg");
                            findItemImageUrlList.add(findItemImageUrl);
                        }
                        newinfor.setTxt_imglist(findItemImageUrlList);
                        newinforList.add(newinfor);
                    }
                    newsDTO.setFindItemList(newinforList);

                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(t, FindDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    findItemList.clear();

                    findItemList.addAll(newsDTO.getFindItemList());

                    Intent intent = new Intent(SplashActivity.this, DiscoverDetailsActivity.class);
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_ID, findItemList.get(0).getTxt_id());
                    intent.putExtra(DiscoverDetailsActivity.DISCOVER_DETAILS_TITLE, findItemList.get(0).getTxt_name());
                    intent.putExtra("FLAG", "isAD");

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscoverDetailsActivity.HEAD_BEAN, findItemList.get(0));
                    intent.putExtras(bundle);
                    startActivity(intent);

                    finish();

                } else {
                    serviceErrot();
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
                serviceErrot();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.FindList);
    }

    private void serviceErrot() {
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}
