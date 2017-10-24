package com.yishuqiao.activity.mine;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.utils.Utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017-7-4 下午4:36:13 项目名称：YiShuQiao 文件名称：BusinessActivity.java 类说明：商务合作界面
 */
public class BusinessActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        title.setText("商务合作");
    }

    @OnClick(R.id.sendEmail)
    public void sendEmail(TextView sendEmail) {
        Intent it = new Intent(Intent.ACTION_SENDTO);
        it.setData(Uri.parse("mailto:" + getString(R.string.business_email)));

        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(it, PackageManager.MATCH_DEFAULT_ONLY);
        if (0 < activities.size()) {
            startActivity(it);
        } else {
            Utils.showToast(this, "没有安装任何邮件客户端");
        }
    }

    @OnClick(R.id.callBusinessTel)
    public void callBusinessTel(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + this.getResources().getString(R.string.tel_num).replace("-", "")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onFinish(View view) {
        finish();
    }

}
