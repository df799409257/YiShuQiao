package com.yishuqiao.activity.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yishuqiao.R;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItem;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItemAdapter;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItems;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.fragment.MyPublishedDisscoverFragment;
import com.yishuqiao.fragment.MyPublishedNewsFragment;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName MyPublishedActivity
 * @Description TODO(我发起的界面)
 * @Date 2017年7月29日 下午8:19:16
 */
public class MyPublishedActivity extends FragmentActivity {

    private TextView title = null;

    private ViewPager viewPager = null;
    private FragmentPagerItemAdapter adapter = null;
    private RadioGroup rg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypublished);

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

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的发起");

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        rg = (RadioGroup) findViewById(R.id.rg);
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

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

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
        pages.add(FragmentPagerItem.of("发现", MyPublishedDisscoverFragment.class));
        pages.add(FragmentPagerItem.of("资讯", MyPublishedNewsFragment.class));

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);

    }

    public void onFinish(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {

        super.onActivityResult(arg0, arg1, arg2);
    }

}
