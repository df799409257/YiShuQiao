package com.yishuqiao.activity.yiyuan;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.core.Arrays;
import net.tsz.afinal.http.AjaxParams;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.HomeActivity;
import com.yishuqiao.activity.ShareActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.ShareDTO;
import com.yishuqiao.dto.ShareDTO.ShareList;
import com.yishuqiao.dto.WriterDetailsDTO;
import com.yishuqiao.dto.WriterDetailsDTO.ArtistInforDetail;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.utils.Utils;

import org.xutils.x;

/**
 * 作者：admin 创建时间：2017年7月14日 下午4:17:34 项目名称：YiShuQiao 文件名称：WriterDetailsActivity.java 类说明：作家详情界面
 */
public class WriterDetailsActivity extends AppCompatActivity {

    public static String ITEMID = "ITEMID";

    private String ItemID = "";

    private String WriterID = "";// 作家ID

    @Bind(R.id.tv_title)
    TextView tv_title;// 标题

    @Bind(R.id.tv_call)
    TextView tv_call;// 电话号码

    @Bind(R.id.rg_group) // 按钮
            RadioGroup rg_group;

    @Bind(R.id.coverPic)
    ImageView coverPic;// 顶部作品图片

    @Bind(R.id.contenter)
    FrameLayout contenter;// 内容容器

    @Bind(R.id.favorite_icon)
    TextView favorite_icon;// 收藏标志

    @Bind(R.id.praise_icon)
    TextView praise_icon;// 点赞标志

    // RadioButton的id集合
    List<Integer> buttonList = Arrays.asList(R.id.rb_work, R.id.rb_resume, R.id.rb_comment, R.id.rb_album,
            R.id.rb_activity);

    private WorkFragment workfragmen = null;
    private AlbumFragment albumfragmen = null;
    private ResumeFragment resumefragmen = null;
    private CommentFragment commentFragment = null;
    private ActivityFragment activityFragment = null;

    private FragmentTransaction transaction;

    private ArtistInforDetail artInfo = null;

    private DownLoadImage downLoadImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_writerdetails);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        MyApplication.getInstance().addActivity(this);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        downLoadImage = new DownLoadImage(this);

        if (getIntent().hasExtra(ITEMID)) {
            ItemID = getIntent().getStringExtra(ITEMID);
        }

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = null;
                for (int i = 0; i < buttonList.size(); i++) {

                    if (buttonList.get(i) == checkedId) {
                        radioButton = (RadioButton) group.findViewById(buttonList.get(i));
                        radioButton.setTextSize(16);
                        setTabSelection(i + 1);
                    } else {
                        radioButton = (RadioButton) group.findViewById(buttonList.get(i));
                        radioButton.setTextSize(14);
                    }
                }

            }

        });

        setTabSelection(1);

        getDataFromService();

    }

    private void getDataFromService() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_text", ItemID);
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (Utils.isGson(t)) {

                    // TODO Auto-generated method stub
                    WriterDetailsDTO newsDTO = new WriterDetailsDTO();
                    if (Conn.IsNetWork) {

                        newsDTO.setTxt_code("0");
                        newsDTO.setTxt_message("请求成功");

                        List<ArtistInforDetail> artInfoDetail = new ArrayList<WriterDetailsDTO.ArtistInforDetail>();

                        for (int i = 0; i < 1; i++) {
                            ArtistInforDetail dto = new ArtistInforDetail();
                            dto.setTxt_cover("http://img.yishuqiao.cn/gallery/img/430946850.jpg");
                            dto.setTxt_id(i + "");
                            dto.setTxt_name("测试作家");
                            dto.setTxt_tel("18210923119");
                            artInfoDetail.add(dto);
                        }
                        newsDTO.setArtistInforDetail(artInfoDetail);

                    } else {
                        Gson gson = new Gson();
                        newsDTO = gson.fromJson(t, WriterDetailsDTO.class);
                    }

                    if (newsDTO.getTxt_code().equals("0")) {

                        if (!(newsDTO.getArtistInforDetail().isEmpty())) {

                            artInfo = newsDTO.getArtistInforDetail().get(0);

                            if (!TextUtils.isEmpty(artInfo.getTxt_name())) {
                                tv_title.setText(artInfo.getTxt_name());
                            }

                            if (!TextUtils.isEmpty(artInfo.getTxt_tel())) {
                                tv_call.setText(artInfo.getTxt_tel());
                            }

                            if (!TextUtils.isEmpty(artInfo.getTxt_cover())) {
                                Log.e("zsy","artInfo.getTxt_cover()="+artInfo.getTxt_cover());
//                                downLoadImage.DisplayImage(artInfo.getTxt_cover(), coverPic);
                                x.image().bind(coverPic,artInfo.getTxt_cover());
                            }

                            WriterID = null != artInfo.getTxt_id() && !TextUtils.isEmpty(artInfo.getTxt_id())
                                    ? artInfo.getTxt_id() : "";

                            workfragmen.getWriterID();

                            if ((artInfo.getTxt_isfav()).equals("1")) {
                                favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                            } else {
                                favorite_icon.setBackgroundResource(R.drawable.like_selector);
                            }

                            if ((artInfo.getTxt_isSP()).equals("1")) {
                                praise_icon.setBackgroundResource(R.drawable.liked);
                            } else {
                                praise_icon.setBackgroundResource(R.drawable.praise_selector);
                            }

                        }

                    } else {
                        Utils.showToast(WriterDetailsActivity.this, newsDTO.getTxt_message());
                    }
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
            }
        }, WriterDetailsActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.ArtDetail);

    }

    // 收藏按钮
    @OnClick(R.id.favorite_icon)
    public void favorite(TextView favorite) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        if (artInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "3");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", ItemID);
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                // TODO Auto-generated method stub
                PublicDTO newsDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    newsDTO.setTxt_code("0");
                    newsDTO.setTxt_message("收藏成功");

                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    if ((artInfo.getTxt_isfav()).equals("1")) {
                        favorite_icon.setBackgroundResource(R.drawable.like_selector);
                        artInfo.setTxt_isfav("0");
                        Utils.showToast(WriterDetailsActivity.this, "取消收藏成功");

                    } else {
                        favorite_icon.setBackgroundResource(R.drawable.favorited_icon);
                        artInfo.setTxt_isfav("1");
                        Utils.showToast(WriterDetailsActivity.this, "收藏成功");

                    }

                } else {
                    Utils.showToast(WriterDetailsActivity.this, newsDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
            }
        }, WriterDetailsActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 点赞按钮
    @OnClick(R.id.praise_icon)
    public void praise(TextView praise) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        if (artInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "3");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", ItemID);
        ajaxParams.put("txt_cate", "0");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                // TODO Auto-generated method stub
                PublicDTO newsDTO = new PublicDTO();
                if (Conn.IsNetWork) {

                    newsDTO.setTxt_code("0");
                    newsDTO.setTxt_message("点赞成功");

                } else {
                    Gson gson = new Gson();
                    newsDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (newsDTO.getTxt_code().equals("0")) {

                    if ((artInfo.getTxt_isSP()).equals("1")) {
                        praise_icon.setBackgroundResource(R.drawable.praise_selector);
                        artInfo.setTxt_isSP("0");
                        Utils.showToast(WriterDetailsActivity.this, "取消点赞成功");

                    } else {
                        praise_icon.setBackgroundResource(R.drawable.liked);
                        artInfo.setTxt_isSP("1");
                        Utils.showToast(WriterDetailsActivity.this, "点赞成功");

                    }

                } else {
                    Utils.showToast(WriterDetailsActivity.this, newsDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                // TODO Auto-generated method stub
            }

            @Override
            public void error() {
                // TODO Auto-generated method stub
            }
        }, WriterDetailsActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 分享按钮
    @OnClick(R.id.share_icon)
    public void share(TextView share) {

        if (!Utils.checkAnony(this)) {
            return;
        }

        if (artInfo == null) {
            return;
        }

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "3");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_workid", ItemID);
        ajaxParams.put("txt_cate", "3");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                ShareDTO dataDTO = new ShareDTO();
                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");

                    ShareList share = dataDTO.new ShareList();
                    share.setTxt_id("1799");
                    share.setTxt_img("http://img.yishuqiao.cn/news/img/1470315032794085002.png");
                    share.setTxt_title("测试标题1");
                    share.setTxt_url("http://www.baidu.com");

                    dataDTO.setShareList(share);

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, ShareDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {

                    // Utils.showToast(DiscoverDetailsActivity.this, dataDTO.getTxt_message());

                    Intent intent = new Intent(WriterDetailsActivity.this, ShareActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShareActivity.SHARE_CONTENT, dataDTO.getShareList());
                    intent.putExtras(bundle);

                    startActivity(intent);

                } else {
                    Utils.showToast(WriterDetailsActivity.this, dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
                Utils.showToast(WriterDetailsActivity.this, "网络连接失败");
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Action);

    }

    // 拨打电话
    public void callCustomService(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + this.getResources().getString(R.string.tel_num).replace("-", "")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setTabSelection(int index) {

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        transaction = getSupportFragmentManager().beginTransaction();
        // 设置动画效果
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        hideFragments(transaction);

        switch (index) {
            case 1:
                if (workfragmen == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    workfragmen = new WorkFragment();
                    transaction.add(R.id.contenter, workfragmen);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(workfragmen);
                }
                break;
            case 2:
                if (resumefragmen == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    resumefragmen = new ResumeFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("WriterID", WriterID);
                    resumefragmen.setArguments(bundle);

                    transaction.add(R.id.contenter, resumefragmen);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(resumefragmen);
                }
                break;
            case 3:
                if (commentFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    commentFragment = new CommentFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("WriterID", WriterID);
                    commentFragment.setArguments(bundle);

                    transaction.add(R.id.contenter, commentFragment);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(commentFragment);
                }
                break;
            case 4:
                if (albumfragmen == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    albumfragmen = new AlbumFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("WriterID", WriterID);
                    albumfragmen.setArguments(bundle);

                    transaction.add(R.id.contenter, albumfragmen);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(albumfragmen);
                }
                break;
            case 5:
                if (activityFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    activityFragment = new ActivityFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("WriterID", WriterID);
                    activityFragment.setArguments(bundle);

                    transaction.add(R.id.contenter, activityFragment);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(activityFragment);
                }
                break;

        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (workfragmen != null) {
            transaction.hide(workfragmen);
        }
        if (resumefragmen != null) {
            transaction.hide(resumefragmen);
        }
        if (albumfragmen != null) {
            transaction.hide(albumfragmen);
        }
        if (commentFragment != null) {
            transaction.hide(commentFragment);
        }
        if (activityFragment != null) {
            transaction.hide(activityFragment);
        }

    }

    public String getWriterID() {
        return WriterID;
    }

    public void onFinish(View view) {

        if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getIntent().hasExtra("FLAG") && getIntent().getStringExtra("FLAG").equals("isAD")) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

}
