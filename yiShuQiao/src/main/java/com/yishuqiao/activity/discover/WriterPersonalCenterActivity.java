package com.yishuqiao.activity.discover;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yishuqiao.R;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItem;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItemAdapter;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItems;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.dto.WriterCenterDTO;
import com.yishuqiao.dto.WriterCenterDTO.UserIndex;
import com.yishuqiao.fragment.WriterDisscoverFragment;
import com.yishuqiao.fragment.WriterNewsFragment;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.BlurBitmap;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.RoundImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.tsz.afinal.http.AjaxParams;

import static android.R.id.list;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName WriterPersonalCenterActivity
 * @Description TODO(发现详情、关注、粉丝带你头像进入作者个人中)
 * @Date 2017年8月9日 下午4:31:48
 */
public class WriterPersonalCenterActivity extends AppCompatActivity {

    private static final int DISMISS = 1001;
    private static final int SHOW = 1002;
    private CustomProgressDialog progressDialog = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    if (progressDialog != null) {
                        progressDialog.setTouchAble((Boolean) msg.obj);
                        progressDialog.show();
                    }
                    break;
                case DISMISS:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private View failedView = null;

    public static final String USERID = "userid";

    public static final String TITLE = "title";

    private String userid = "";

    private FragmentPagerItemAdapter adapter = null;

    private Bundle bundle;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.rg)
    RadioGroup rg;

    @Bind(R.id.im_url)
    RoundImageView userPic;// 用户头像

    @Bind(R.id.rl_writerBG)
    RelativeLayout rl_writerBG;// 用户名

    @Bind(R.id.iv_writerBG)
    ImageView iv_writerBG;// 用户名

    @Bind(R.id.name)
    TextView name;// 用户名

    @Bind(R.id.attentionNum)
    TextView attentionNum;// 关注数量

    @Bind(R.id.fansNum)
    TextView fansNum;// 粉丝数量

    @Bind(R.id.attention_bt)
    TextView attention_bt;// 关注按钮

    @Bind(R.id.ll_content)
    LinearLayout ll_content;// 内容

    private RequestQueue mQueue;

    private boolean isFollow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_writercenter);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        if (getIntent().hasExtra(USERID)) {
            userid = getIntent().getStringExtra(USERID);
        }

        bundle = new Bundle();
        bundle.putString(USERID, userid);

        mQueue = Volley.newRequestQueue(this);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.involved) {
                    viewPager.setCurrentItem(0);
                } else {
                    viewPager.setCurrentItem(1);
                }

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 0) {
                    if (rg.getCheckedRadioButtonId() != R.id.involved) {
                        rg.check(R.id.involved);
                    }
                }

                if (arg0 == 1) {
                    if (rg.getCheckedRadioButtonId() != R.id.message) {
                        rg.check(R.id.message);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("发现", WriterDisscoverFragment.class, bundle));
        pages.add(FragmentPagerItem.of("资讯", WriterNewsFragment.class, bundle));

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);

        getDataFromServer();

    }

    private void getDataFromServer() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "4");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_uid", userid);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                removeFailedView();
                dismissLoadingView();

                WriterCenterDTO dto = new WriterCenterDTO();
                UserIndex userIndex = dto.new UserIndex();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("关注成功");

                    userIndex.setTxt_fans("0");
                    userIndex.setTxt_follow("0");
                    userIndex.setTxt_isfollow("0");
                    userIndex.setTxt_userid("0");
                    userIndex.setTxt_username("呵呵");
                    userIndex.setTxt_userpic("");

                    dto.setUserIndex(userIndex);

                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, WriterCenterDTO.class);
                }

                if (dto.getTxt_code().equals("0")) {
                    userIndex = dto.getUserIndex();
                    if (!TextUtils.isEmpty(userIndex.getTxt_userpic())) {
                        ImageLoader.getInstance().displayImage(userIndex.getTxt_userpic(), userPic,
                                new ImageLoadingListener() {

                                    @Override
                                    public void onLoadingStarted(String arg0, View arg1) {
                                    }

                                    @Override
                                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                        userPic.setImageResource(R.drawable.default_user_icon);
                                    }

                                    @Override
                                    public void onLoadingComplete(String arg0, View arg1, Bitmap response) {
                                        if (response != null)
                                            iv_writerBG.setImageBitmap(BlurBitmap.blur(WriterPersonalCenterActivity.this, response));

                                        // rl_writerBG.setBackground(new BitmapDrawable(getResources(), BlurBitmap.blur(WriterPersonalCenterActivity.this, response)));
                                        userPic.setImageBitmap(response);

                                        ll_content.setVisibility(View.VISIBLE);
                                        dismissLoadingView();
                                    }

                                    @Override
                                    public void onLoadingCancelled(String arg0, View arg1) {
                                    }
                                });

                    }

                    name.setText(TextUtils.isEmpty(userIndex.getTxt_username()) ? "" : userIndex.getTxt_username());
                    attentionNum.setText(
                            TextUtils.isEmpty(userIndex.getTxt_follow()) ? "" : "关注 : " + userIndex.getTxt_follow());
                    fansNum.setText(
                            TextUtils.isEmpty(userIndex.getTxt_fans()) ? "" : "粉丝 : " + userIndex.getTxt_fans());

                    if (userIndex.getTxt_isfollow().equals("0")) {
                        isFollow = true;
                        attention_bt.setText("关注");
                        attention_bt.setBackgroundResource(R.drawable.corners_red_bg);
                    } else {
                        isFollow = false;
                        attention_bt.setText("已关注");
                        attention_bt.setBackgroundResource(R.drawable.corners_gray_bg);
                    }

                    attention_bt.setVisibility(View.VISIBLE);

                } else {
                    Utils.showToast(WriterPersonalCenterActivity.this, dto.getTxt_message());
                    addFailedView();
                }
            }

            @Override
            public void start() {
                showLoadingView();
            }

            @Override
            public void error() {
                dismissLoadingView();
                addFailedView();
                Utils.showToast(WriterPersonalCenterActivity.this, "网络连接失败");
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/follow.php");

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {

        super.onActivityResult(arg0, arg1, arg2);
    }

    public void onFinish(View view) {
        finish();
    }

    /**
     * @Description (关注或取消关注)
     */
    @OnClick(R.id.attention_bt)
    public void submitAttention(TextView tv) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("pa_user_id", userid);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("关注成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    Utils.showToast(WriterPersonalCenterActivity.this, dto.getTip());
                    if (isFollow) {
                        attention_bt.setText("已关注");
                        attention_bt.setBackgroundResource(R.drawable.corners_gray_bg);
                    } else {
                        attention_bt.setText("关注");
                        attention_bt.setBackgroundResource(R.drawable.corners_red_bg);
                    }
                    isFollow = !isFollow;
                } else {
                    Utils.showToast(WriterPersonalCenterActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/follow.php");

    }

    protected void addFailedView() {
        if (failedView == null) {
            failedView = getLayoutInflater().inflate(R.layout.net_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.header);
            ll_content.addView(failedView, params);
            failedView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getDataFromServer();
                }
            });
        }
    }

    protected void removeFailedView() {
        if (failedView != null) {
            ll_content.removeView(failedView);
            failedView = null;
        }
    }

    /**
     * 显示加载视图
     *
     * @param isTouchAble true:可点击 false:不可点击 默认是可点击
     */
    protected void showLoadingView(boolean isTouchAble) {
        Message m = mHandler.obtainMessage(SHOW, isTouchAble);
        mHandler.sendMessage(m);
    }

    protected void showLoadingView() {
        showLoadingView(true);
    }

    /**
     * 关闭加载视图
     */
    protected void dismissLoadingView() {
        mHandler.sendEmptyMessage(DISMISS);
    }

    protected boolean isShowingLoadingView() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
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

}
