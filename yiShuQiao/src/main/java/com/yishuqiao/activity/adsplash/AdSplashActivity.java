package com.yishuqiao.activity.adsplash;

import java.io.File;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.HomeActivity;
import com.yishuqiao.activity.SplashActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.utils.Conn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName AdSplashActivity
 * @Description TODO(带广告的splash界面)
 * @Date 2017年7月21日 下午7:06:39
 */
public class AdSplashActivity extends BaseActivity {

    private ImageView logo;
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (MyApplication.MySharedPreferences.readIsYinDao()) {
                startActivity(new Intent(AdSplashActivity.this, SplashActivity.class));
            } else {
                startActivity(new Intent(AdSplashActivity.this, HomeActivity.class));
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        logo = (ImageView) findViewById(R.id.logo);

        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        logo.startAnimation(animation);
        handler.postDelayed(runnable, 3000);

        Conn.sdCardPathDown = Environment.getExternalStorageDirectory().toString() + "/YSQ";
        File filedown = new File(Conn.sdCardPathDown);
        if (!filedown.exists()) {
            filedown.mkdirs();
        }

    }

}
