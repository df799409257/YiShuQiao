package com.yishuqiao.activity.mine;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.MyFansDTO;
import com.yishuqiao.dto.MyFansDTO.FollowInforList;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.MyToast;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.MultiStateView.ViewState;

/**
 * 作者：admin 创建时间：2017-7-4 下午4:34:05 项目名称：YiShuQiao 文件名称：FeedbackActivity.java 类说明：意见反馈界面
 */
public class FeedbackActivity extends BaseActivity {

    private TextView title = null;
    private TextView onSubmit = null;
    private static final int MAX_COUNT = 150;// 最大字数
    private EditText otherEditText = null;
    private TextView fontNumTextView = null; // 字数显示

    private MyToast myToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        myToast = new MyToast(this);

        initView();
    }

    private void initView() {

        title = (TextView) findViewById(R.id.tv_title);
        title.setText("意见反馈");

        onSubmit = (TextView) findViewById(R.id.onSubmit);
        onSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onSubmit(v);
            }
        });

        fontNumTextView = (TextView) findViewById(R.id.tv_fontNum);
        otherEditText = (EditText) findViewById(R.id.et_content);
        otherEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int strCount = Utils.strCharacterCount(otherEditText.getText().toString().trim());
                fontNumTextView.setText(strCount / 2 + "/" + MAX_COUNT + "");

                if (strCount > MAX_COUNT * 2) {
                    fontNumTextView.setTextColor(Color.rgb(255, 251, 71));
                } else {
                    fontNumTextView.setTextColor(Color.rgb(255, 156, 156));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

        });

    }

    public void onSubmit(final View view) {
        if (Utils.strCharacterCount(otherEditText.getText().toString().trim()) / 2 > MAX_COUNT) {
            Utils.showToast(this, "您的说明应在" + MAX_COUNT + "个汉字以内");
            return;
        }

        if (TextUtils.isEmpty(otherEditText.getText().toString())) {
            Utils.showToast(this, "请填写反馈内容");
            return;
        }
        if (Utils.checkAnony(this)) {
            submit(view);
        }

    }

    // 提交信息
    private void submit(final View view) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_content", otherEditText.getText().toString());
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                dismissLoadingView();
                view.setEnabled(true);

                PublicDTO dataDTO = new PublicDTO();

                if (Conn.IsNetWork) {

                    dataDTO.setTxt_code("0");
                    dataDTO.setTxt_message("请求成功");
                    dataDTO.setTip("提交成功");

                } else {
                    Gson gson = new Gson();
                    dataDTO = gson.fromJson(t, PublicDTO.class);
                }
                if (dataDTO.getTxt_code().equals("0")) {
                    myToast.show(dataDTO.getTip());
                } else {
                    myToast.show(dataDTO.getTxt_message());
                }
            }

            @Override
            public void start() {
                view.setEnabled(false);
                showLoadingView();
            }

            @Override
            public void error() {
                view.setEnabled(true);
                dismissLoadingView();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.UpDate);

    }

    @Override
    protected void onPause() {
        if (myToast != null) {
            myToast.cancel();
        }
        super.onPause();
    }

    public void onFinish(View view) {
        finish();
    }

}
