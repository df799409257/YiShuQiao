package com.yishuqiao.activity.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;

/**
 * 作者：admin 创建时间：2017-7-4 下午3:06:08 项目名称：YiShuQiao 文件名称：AboutUsActivity.java 类说明：关于我们界面
 */
public class AboutUsActivity extends BaseActivity {

    private TextView title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

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
        title.setText("关于我们");

        TextView textView1 = (TextView) findViewById(R.id.text1);
        textView1.setText(Html.fromHtml(getString(R.string.about_us_discovery)));
        TextView textView2 = (TextView) findViewById(R.id.text2);
        textView2.setText((Html.fromHtml(getString(R.string.about_us_news))));
        TextView textView3 = (TextView) findViewById(R.id.text3);
        textView3.setText(Html.fromHtml(getString(R.string.about_us_video)));
        TextView textView4 = (TextView) findViewById(R.id.text4);
        textView4.setText(Html.fromHtml(getString(R.string.about_us_gallery)));

    }

    public void callCustomService(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + this.getResources().getString(R.string.tel_num).replace("-", "")));
        Log.e("phoneNum=", this.getResources().getString(R.string.tel_num).replace("-", ""));
        startActivity(intent);
    }

    public void onFinish(View view) {
        finish();
    }

}
