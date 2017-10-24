package com.yishuqiao.activity.mine;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItem;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItemAdapter;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItems;
import com.yishuqiao.fragment.FavoriteDiscoverFragment;
import com.yishuqiao.fragment.FavoriteYiyuanFragment;
import com.yishuqiao.fragment.InformationFragment;
import com.yishuqiao.fragment.VideoFragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017-7-4 下午3:00:43 项目名称：YiShuQiao 文件名称：FavoriteActivity.java 类说明：我的收藏界面
 */
public class FavoriteActivity extends BaseActivity {

    private TextView title = null;

    private ViewPager viewPager = null;
    private FragmentPagerItemAdapter adapter = null;
    private RadioGroup rg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favcorte);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
    }

    private void initView() {

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的收藏");

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.discover:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.involved:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.message:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.yiyuan:
                        viewPager.setCurrentItem(3);
                        break;

                    default:
                        break;
                }

            }
        });

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                switch (arg0) {
                    case 0:
                        if (rg.getCheckedRadioButtonId() != R.id.discover) {
                            rg.check(R.id.discover);
                        }
                        break;
                    case 1:
                        if (rg.getCheckedRadioButtonId() != R.id.involved) {
                            rg.check(R.id.involved);
                        }
                        break;
                    case 2:
                        if (rg.getCheckedRadioButtonId() != R.id.message) {
                            rg.check(R.id.message);
                        }
                        break;
                    case 3:
                        if (rg.getCheckedRadioButtonId() != R.id.yiyuan) {
                            rg.check(R.id.yiyuan);
                        }
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

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("发现", FavoriteDiscoverFragment.class));
        pages.add(FragmentPagerItem.of("资讯", InformationFragment.class));
        pages.add(FragmentPagerItem.of("视屏", VideoFragment.class));
        pages.add(FragmentPagerItem.of("艺苑", FavoriteYiyuanFragment.class));

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);

    }

    public void onFinish(View view) {
        finish();
    }

}
