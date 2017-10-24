package com.yishuqiao.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.discover.DiscoverDetailsActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.activity.yiyuan.WriterDetailsActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.dto.SplashDTO;
import com.yishuqiao.dto.SplashDTO.Splash;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxParams;

/**
 * 欢迎页
 *
 * @author Administrator
 */
public class WelComeActivity extends BaseActivity {

    private List<FindItem> findItemList = new ArrayList<FindDTO.FindItem>();
    private ImageView logo;
    private ImageView logo_ad;
    private LinearLayout ll_content;
    private TextView skip;
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (MyApplication.MySharedPreferences.readIsYinDao()) {
                startActivity(new Intent(WelComeActivity.this, SplashActivity.class));
            } else {
                startActivity(new Intent(WelComeActivity.this, HomeActivity.class));
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        /* set it to be full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        Conn.sdCardPathDown = Environment.getExternalStorageDirectory().toString() + "/YSQ";
        File filedown = new File(Conn.sdCardPathDown);
        if (!filedown.exists()) {
            filedown.mkdirs();
        }

        initView();
    }

    private void initView() {

        logo = (ImageView) findViewById(R.id.logo);
        skip = (TextView) findViewById(R.id.skip);

        logo_ad = (ImageView) findViewById(R.id.logo_ad);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        getAD();

        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        logo.startAnimation(animation);
        handler.postDelayed(runnable, 5000);

    }

    private void getAD() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                SplashDTO dto = new SplashDTO();

                if (Conn.IsNetWork) {

                    dto.setTxt_code("0");
                    dto.setTxt_message("请求成功");

                    Splash splash = dto.new Splash();
                    splash.setTxt_id("0");
                    splash.setTxt_pic("");
                    splash.setTxt_type("2");
                    dto.setSplash(splash);

                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, SplashDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    final Splash splash = dto.getSplash();

                    skip.setVisibility(View.VISIBLE);
                    skip.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            handler.removeCallbacks(runnable);
                            if (MyApplication.MySharedPreferences.readIsYinDao()) {
                                startActivity(new Intent(WelComeActivity.this, SplashActivity.class));
                            } else {
                                startActivity(new Intent(WelComeActivity.this, HomeActivity.class));
                            }
                            finish();
                        }
                    });

                    logo.clearAnimation();
                    if (!TextUtils.isEmpty(splash.getTxt_pic())) {
                        Picasso.with(WelComeActivity.this).load(splash.getTxt_pic()).fit().into(logo_ad);
                        logo.setVisibility(View.GONE);
                        ll_content.setVisibility(View.VISIBLE);
                        logo_ad.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                handler.removeCallbacks(runnable);
                                toNext(splash.getTxt_id(), splash.getTxt_type());
                            }

                        });
                    }

                } else {
//                    serviceErrot();
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
//                serviceErrot();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.AD);

    }

    private void toNext(String id, String type) {

        if (MyApplication.MySharedPreferences.readIsYinDao()) {
            Intent intent = new Intent(WelComeActivity.this, SplashActivity.class);
            intent.putExtra(SplashActivity.ID, id);
            intent.putExtra(SplashActivity.TYPE, type);
            startActivity(intent);
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

                    Intent intent = new Intent(WelComeActivity.this, DiscoverDetailsActivity.class);
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
        if (MyApplication.MySharedPreferences.readIsYinDao()) {
            startActivity(new Intent(WelComeActivity.this, SplashActivity.class));
        } else {
            startActivity(new Intent(WelComeActivity.this, HomeActivity.class));
        }
        finish();
    }

}
