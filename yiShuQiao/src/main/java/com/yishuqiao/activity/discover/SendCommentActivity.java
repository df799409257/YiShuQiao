package com.yishuqiao.activity.discover;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.MyToast;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName SendCommentActivity
 * @Description TODO(发表评论界面)
 * @Date 2017年7月26日 下午1:50:49
 */
public class SendCommentActivity extends BaseActivity implements OnClickListener {

    public static final String HINT = "HINT";
    public static final String URL = "URL";// 发表接口地址
    public static final String CONTENT = "CONTENT";// 发表的内容参数名
    public static final String PARAMS = "PARAMS";// 参数
    private TextView sendView = null;
    private EditText contentEditText = null;
    private CustomProgressDialog progressDialog = null;
    private Map<String, String> params = null;
    private String url = null;
    private String content = null;
    private String hint = "说点什么吧......";

    private MyToast myToast = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_comment_layout);

        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);


        myToast = new MyToast(this);

        if (getIntent().hasExtra(URL)) {
            url = getIntent().getStringExtra(URL);
        }
        if (getIntent().hasExtra(CONTENT)) {
            content = getIntent().getStringExtra(CONTENT);
        }
        if (getIntent().hasExtra(PARAMS)) {
            params = (Map<String, String>) getIntent().getSerializableExtra(PARAMS);
        }
        if (getIntent().hasExtra(HINT)) {
            hint = getIntent().getStringExtra(HINT);
        }
        initView();
        setFinishOnTouchOutside(true);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }

        super.onResume();
    }

    private void initView() {

        sendView = (TextView) findViewById(R.id.tv_send);
        sendView.setOnClickListener(this);
        contentEditText = (EditText) findViewById(R.id.et_content);
        if (!TextUtils.isEmpty(HINT)) {
            contentEditText.setHint(hint);
        }
        contentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send: // 发送按钮
                // TODO 提交操作
                if (Utils.checkAnony(this)) {
                    if (TextUtils.isEmpty(contentEditText.getText().toString())) {
                        Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    params.put(content, contentEditText.getText().toString());
                    params.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
                    params.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
                    submit(params, url);
                }
                break;
            default:
                break;
        }
    }

    // 提交信息
    private void submit(final Map<String, String> params, final String url) {

        AjaxParams ajaxParams = new AjaxParams();

        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            ajaxParams.put(entry.getKey(), entry.getValue());
        }

        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("提交成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    SendCommentActivity.this.setResult(RESULT_OK);

                    myToast.show(dto.getTxt_message());

                    finish();

                } else {
                    myToast.show(dto.getTxt_message());
                }

            }

            @Override
            public void start() {
                progressDialog.setTouchAble(false);
                progressDialog.show();
            }

            @Override
            public void error() {
                myToast.show("网络连接失败");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, url);

    }

    @Override
    protected void onPause() {
        if (myToast != null) {
            myToast.cancel();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

}
