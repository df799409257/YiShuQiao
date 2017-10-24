package com.yishuqiao.activity.mine;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.fragment.CommonLoginFragment;
import com.yishuqiao.fragment.FastLoginFragment;

/**
 * 作者：admin 创建时间：2017-7-5 上午10:24:26 项目名称：YiShuQiao 文件名称：LoginActivity.java 类说明：登录界面
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private RadioGroup rg = null;
    private ViewPager login_viewpager;
    private List<Fragment> fragments;

    private RadioButton rb_fast_login;
    private RadioButton rg_login_common;

    private TextView tv_title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        MyApplication.getInstance().addActivity(this);

        initView();

    }

    private void initView() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("登录");

        login_viewpager = (ViewPager) findViewById(R.id.login_viewpager);
        rg = (RadioGroup) findViewById(R.id.rg_login);
        rb_fast_login = (RadioButton) findViewById(R.id.rb_fast_login);
        rb_fast_login.setOnClickListener(this);
        rg_login_common = (RadioButton) findViewById(R.id.rg_login_common);
        rg_login_common.setOnClickListener(this);
        fragments = new ArrayList<Fragment>();
        fragments.add(new FastLoginFragment());
        fragments.add(new CommonLoginFragment());

        login_viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });

        // 添加页面切换事件的监听器
        login_viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rg.check(R.id.rb_fast_login);
                        break;
                    case 1:
                        rg.check(R.id.rg_login_common);
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        rg.check(R.id.rb_fast_login);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rg_login_common:
                login_viewpager.setCurrentItem(1, true);
                break;
            case R.id.rb_fast_login:
                login_viewpager.setCurrentItem(0, true);
                break;

            default:
                break;
        }
    }

    public void onFinish(View view) {
        finish();
    }

}
