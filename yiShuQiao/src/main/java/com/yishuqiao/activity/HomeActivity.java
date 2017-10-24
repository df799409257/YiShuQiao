package com.yishuqiao.activity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.mine.AboutUsActivity;
import com.yishuqiao.activity.mine.FavoriteActivity;
import com.yishuqiao.activity.mine.MessageCenterActivity;
import com.yishuqiao.activity.mine.MyAttentionActivity;
import com.yishuqiao.activity.mine.MyFansActivity;
import com.yishuqiao.activity.mine.MyFollowGalleryActivity;
import com.yishuqiao.activity.mine.MyFollowWorksActivity;
import com.yishuqiao.activity.mine.MyIntegralActivity;
import com.yishuqiao.activity.mine.MyPublishedActivity;
import com.yishuqiao.activity.mine.PersonalInfoActivity;
import com.yishuqiao.activity.mine.SystemSettingActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.InForMationDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.fragment.FragmentFirst;
import com.yishuqiao.fragment.FragmentFour;
import com.yishuqiao.fragment.FragmentThree;
import com.yishuqiao.fragment.FragmentTow;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.NetUtil;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.utils.ViewColor;
import com.yishuqiao.view.CircleImageView;
import com.yishuqiao.view.slidingmenu.SlidingMenu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import net.tsz.afinal.http.AjaxParams;

/**
 * 主界面
 *
 * @author Administrator
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint({"InlinedApi", "NewApi"})
public class HomeActivity extends BaseActivity implements OnCheckedChangeListener{
    /**
     * 网络类型
     */
    private int netMobile;

    private int pageIndex = 1;

    private int select = 0;

    // 机关推送相关信息
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;

    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;
    /**
     * 上次点击返回键的时间
     */
    private static final int QUIT_INTERVAL = 2000;

    private FragmentTransaction transaction;
    private RadioGroup radioGroup;
    private RadioButton rb_discover;

    private FragmentFirst fragmentOne;
    private FragmentTow fragmentTwo;
    private FragmentThree fragmentThree;
    private FragmentFour fragmentFour;

    private SlidingMenu slidingMenu;

    private RelativeLayout re_back;

    private TextView userName, tv_fans_num;
    private CircleImageView userIcon;

    private TextView tv_title;

    private  RelativeLayout re_net;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


                // 设置沉浸式状态栏的颜色
        ViewColor.setColor(this, Color.rgb(251, 72, 71));
        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        MyApplication.getInstance().addActivity(this);

        EventBus.getDefault().register(this);

        // 极光推送相关
        JPushInterface.setAlias(this, MyApplication.MySharedPreferences.readUserid(), null);
        registerMessageReceiver();// 接收消息

        inspectNet();

        initView();

    }

    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(HomeActivity.this);
        return isNetConnect();

    }
    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == NetUtil.NETWORK_WIFI) {
            return true;
        } else if (netMobile == NetUtil.NETWORK_MOBILE) {
            return true;
        } else if (netMobile == NetUtil.NETWORK_NONE) {
            return false;

        }
        return false;
    }
    private void initView() {
        re_net= (RelativeLayout) findViewById(R.id.re_net);
        re_net.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setBehindOffset(260);
        // slidingMenu.setFadeEnabled(true);
        // slidingMenu.setFadeDegree(0.4f);
        slidingMenu.setOffsetFadeDegree(0.4f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.view_slingmenu);

        // 获取slidingMenu的视图view
        View menuView = slidingMenu.getMenu();
        initSlidMenu(menuView);

        setTabSelection(1);

        radioGroup = (RadioGroup) findViewById(R.id.rg_group);
        radioGroup.setOnCheckedChangeListener(this);

        ((RadioButton) radioGroup.findViewById(R.id.rb_discover)).setChecked(true);

        rb_discover = (RadioButton) findViewById(R.id.rb_discover);

        re_back = (RelativeLayout) findViewById(R.id.in_include).findViewById(R.id.re_back);
        re_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                slidingMenu.showMenu();
            }
        });
        getInformation();

    }

    private void initSlidMenu(View menuView) {

        userName = (TextView) menuView.findViewById(R.id.userName);// 用户名
        tv_fans_num = (TextView) menuView.findViewById(R.id.tv_fans_num);// 粉丝数量
        userIcon = (CircleImageView) menuView.findViewById(R.id.userIcon);// 用户头像

        if (MyApplication.MySharedPreferences.readIsLogin()) {

            // tv_fans_num.setVisibility(View.VISIBLE);
            getBubble();

        } else {
            userIcon.setImageDrawable(null);
            userIcon.setBackgroundResource(R.drawable.person_selector);
            userName.setText("你还没有登录，请先登录");
            // tv_fans_num.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_discover:
                setTabSelection(1);
                tv_title.setText("发现");
                select = 0;
                break;
            case R.id.rb_information:
                setTabSelection(2);
                tv_title.setText("资讯");
                select = 1;
                break;
            case R.id.rb_video:
                setTabSelection(3);
                tv_title.setText("视频");
                select = 2;
                break;
            case R.id.rb_gallery:
                setTabSelection(4);
                tv_title.setText("艺苑");
                select = 3;
                break;
        }
    }

    private void setTabSelection(int index) {

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        transaction = getSupportFragmentManager().beginTransaction();
        // 设置动画效果
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        hideFragments(transaction);

        switch (index) {
            case 1:

                if (fragmentOne == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragmentOne = new FragmentFirst();
                    transaction.add(R.id.contenter, fragmentOne);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragmentOne);
                }
                tv_title.setText("发现");

                pageIndex = 1;

                if (!(select == 0)) {
                    fragmentOne.reloadData();
                }

                break;
            case 2:
                if (fragmentTwo == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragmentTwo = new FragmentTow();
                    transaction.add(R.id.contenter, fragmentTwo);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragmentTwo);
                }
                tv_title.setText("资讯");

                pageIndex = 2;

                if (!(select == 1)) {
                    fragmentTwo.reloadData();
                }

                break;
            case 3:
                if (fragmentThree == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragmentThree = new FragmentThree();
                    transaction.add(R.id.contenter, fragmentThree);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragmentThree);
                }
                tv_title.setText("视频");

                pageIndex = 3;

                if (!(select == 2)) {
                    fragmentThree.reloadData();
                }

                break;
            case 4:
                if (fragmentFour == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragmentFour = new FragmentFour();
                    transaction.add(R.id.contenter, fragmentFour);

                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragmentFour);
                }
                tv_title.setText("艺苑");

                pageIndex = 4;

                if (!(select == 3)) {
                    fragmentFour.reloadData();
                }

                break;

        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (fragmentOne != null) {
            transaction.hide(fragmentOne);
        }
        if (fragmentTwo != null) {
            transaction.hide(fragmentTwo);
        }
        if (fragmentThree != null) {
            transaction.hide(fragmentThree);
        }
        if (fragmentFour != null) {
            transaction.hide(fragmentFour);
        }
    }

    // 点击进入设置
    public void onSettingClick(View view) {
        Intent intent = new Intent(this, SystemSettingActivity.class);
        startActivity(intent);
    }

    // 用户界面(需要进行登录判断，没我有登录进入登录界面)
    public void onUserClick(View view) {
        if (Utils.checkAnony(this)) {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        }
    }

    // 消息中心界面(需要做登录判断)
    public void startMessageCenter(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MessageCenterActivity.class));
        }
    }

    // 我的积分界面(需要做登录判断)
    public void startMyIntegral(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyIntegralActivity.class));
        }
    }

    // 我的粉丝界面(需要做登录判断)
    public void startFans(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyFansActivity.class));
        }
    }

    // 我的关注界面(需要做登录判断)
    public void startAttention(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyAttentionActivity.class));
        }
    }

    // 我发起的界面(需要做登录判断)
    public void startMySend(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyPublishedActivity.class));
        }
    }

    // 我的收藏界面(需要做登录判断)
    public void startFavorite(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, FavoriteActivity.class));
        }
    }

    // 我关注的作品界面(需要做登录判断)
    public void startMyFollowWorks(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyFollowWorksActivity.class));
        }
    }

    // 我关注的画廊界面(需要做登录判断)
    public void startMyFollowGallery(View view) {
        if (Utils.checkAnony(this)) {
            startActivity(new Intent(this, MyFollowGalleryActivity.class));
        }
    }

    // 关于我们界面
    public void startAboutUs(View view) {
        startActivity(new Intent(this, AboutUsActivity.class));
    }

    // 拨打电话
    public void callCustomService(View view) {

//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_CALL);
//        intent.setData(Uri.parse("tel:" + this.getResources().getString(R.string.tel_num).replace("-", "")));
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+this.getResources().getString(R.string.tel_num).replace("-", "")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    // 点击进入搜索界面
    public void onSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.TYPE, pageIndex);
        startActivity(intent);
    }

    /**
     * 作者:admin 时间:2017-6-30 下午3:17:09 描述:EventBus消息处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventTypeString msg) {
        String message = msg.getMessage();
        if (message.equals("refresh") || message.equals("Logout") || message.equals("login")) {
            // 刷新侧滑页数据
            refreshSlidMenu();
            getInformation();
            registerMessageReceiver();// 接收消息
        }
    }

    private void refreshSlidMenu() {

        if (MyApplication.MySharedPreferences.readIsLogin()) {

            if ((MyApplication.MySharedPreferences.readPoints()).equals("0")) {
                // tv_fans_num.setVisibility(View.GONE);
            } else {
                // tv_fans_num.setVisibility(View.VISIBLE);
                tv_fans_num.setText(MyApplication.MySharedPreferences.readPoints());
            }

            getBubble();

        } else {

            userIcon.setImageDrawable(null);
            userIcon.setBackgroundResource(R.drawable.person_selector);
            userName.setText("你还没有登录，请先登录");

            // tv_fans_num.setVisibility(View.GONE);

        }
    }

    // 加载侧拉菜单数据
    private void getBubble() {

        if (TextUtils.isEmpty(MyApplication.MySharedPreferences.readUserPic())) {
            userIcon.setImageResource(R.drawable.default_user_icon);
        } else {
            Picasso.with(this).load(MyApplication.MySharedPreferences.readUserPic()).fit()
                    .error(R.drawable.default_user_icon).into(userIcon);
        }

        userName.setText(MyApplication.MySharedPreferences.readUsername());

    }

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);

        unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
                if (fragmentOne.isHidden()) {
                    setTabSelection(1);
                    rb_discover.setChecked(true);
                } else {
                    if (slidingMenu.isMenuShowing()) {
                        slidingMenu.toggle();
                    } else {
                        long backPressed = System.currentTimeMillis();
                        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                            lastBackPressed = backPressed;
                            Toast.makeText(HomeActivity.this, "再按一次退出", Toast.LENGTH_LONG).show();
                        } else {
                            finish();
                            System.exit(0);
                            Utils.clearAppUserData("com.yishuqiao");
                        }
                    }

                }

            }
        }
        return false;
    }


    public void getInformation() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "9");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                InForMationDTO dto = new InForMationDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, InForMationDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    MyApplication.MySharedPreferences.saveGender(dto.getUserInfo().getTxt_user_sex());
                    MyApplication.MySharedPreferences.saveTel(dto.getUserInfo().getTxt_user_tel());
                    MyApplication.MySharedPreferences.saveUsername(dto.getUserInfo().getTxt_nickname());
                    MyApplication.MySharedPreferences.saveUserid(dto.getUserInfo().getTxt_userid());
                    MyApplication.MySharedPreferences.saveRegtime(dto.getUserInfo().getTxt_txt_regtime());
                    MyApplication.MySharedPreferences.savePoints(dto.getUserInfo().getTxt_user_points());
                    MyApplication.MySharedPreferences.saveUserPic(dto.getUserInfo().getTxt_user_pic());

                    if (null != dto.getUserInfo().getTxt_user_push()
                            && (dto.getUserInfo().getTxt_user_push()).equals("0")) {
                        MyApplication.MySharedPreferences.saveIsPush(false);
                    } else if (null != dto.getUserInfo().getTxt_user_push()
                            && (dto.getUserInfo().getTxt_user_push()).equals("1")) {
                        MyApplication.MySharedPreferences.saveIsPush(true);
                    }

                    if ((dto.getUserInfo().getTxt_user_points()).equals("0")) {
                        // tv_fans_num.setVisibility(View.GONE);
                    } else {
                        // tv_fans_num.setText(dto.getUserInfo().getTxt_user_points());
                    }

                    refreshSlidMenu();

                } else {
                    // Utils.showToast(HomeActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, HomeActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/modifyUser.php");

    }

    @Override
    protected void onResume() {
        isForeground = true;

        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!"".equals(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Toast.makeText(HomeActivity.this, messge, Toast.LENGTH_LONG).show();
                // setCostomMsg(showMsg.toString());
            }
        }
    }

    // 设置沉浸状态栏
    private void initWindows() {
        Window window = getWindow();
        int color = getResources().getColor(android.R.color.holo_blue_light);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 设置状态栏颜色
            window.setStatusBarColor(color);
            // 设置导航栏颜色
            window.setNavigationBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            // 给statusbar着色
            View view = new View(this);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

}
