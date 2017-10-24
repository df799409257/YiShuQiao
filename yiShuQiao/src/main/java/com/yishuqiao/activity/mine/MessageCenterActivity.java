package com.yishuqiao.activity.mine;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItem;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItemAdapter;
import com.yishuqiao.adapter.fragmentadapter.FragmentPagerItems;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.fragment.InvolvedFragment;
import com.yishuqiao.fragment.MessageFragment;

/**
 * 作者：admin 创建时间：2017-7-4 下午2:48:35 项目名称：YiShuQiao 文件名称：MessageCenterActivity.java 类说明：消息中心界面
 */
public class MessageCenterActivity extends BaseActivity {

    private TextView title = null;

    private ViewPager viewPager = null;
    private FragmentPagerItemAdapter adapter = null;
    private RadioGroup rg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagecenter);

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
        title.setText("消息中心");

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
        pages.add(FragmentPagerItem.of("我参与的", InvolvedFragment.class));
        pages.add(FragmentPagerItem.of("系统消息", MessageFragment.class));

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);

        if (getIntent().hasExtra("JSON")) {
            try {
                JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("JSON"));
                if (jsonObject.optString("key").equals("1")) {
                    viewPager.setCurrentItem(0);
                } else if (jsonObject.optString("key").equals("2")) {
                    viewPager.setCurrentItem(1);
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

    }

    public void onFinish(View view) {
        finish();
    }

}
