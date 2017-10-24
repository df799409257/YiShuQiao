package com.yishuqiao.activity.mine;

import org.greenrobot.eventbus.EventBus;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ModifyPhoneActivity
 * @Description TODO(修改手机号码)
 * @Date 2017年9月5日 下午6:36:23
 */
public class ModifyPhoneActivity extends BaseActivity {

    private Chronometer sendCheckCode = null;
    private EditText username = null;
    private EditText checkCode = null;
    private Snackbar snackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_phone_layout);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        username = (EditText) findViewById(R.id.userName);
        checkCode = (EditText) findViewById(R.id.et_checkCode);
        sendCheckCode = (Chronometer) findViewById(R.id.ch_sendCheckCode);
        sendCheckCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = username.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Utils.showToast(ModifyPhoneActivity.this, "请输入11位手机号码");
                    // snackbar = Snackbar.make(v, "请输入11位手机号码",
                    // Snackbar.LENGTH_SHORT).setAction("关闭",
                    // new View.OnClickListener() {
                    // @Override
                    // public void onClick(View v) {
                    // snackbar.dismiss();
                    // }
                    // });
                    // snackbar.show();
                    // phone = null;
                    return;
                }
                if (!phone.startsWith("1") || phone.length() != 11) {
                    Utils.showToast(ModifyPhoneActivity.this, "请输入正确的手机号码");
                    phone = null;
                    return;
                }
                sendCheckCode();
            }
        });
        sendCheckCode.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = (Long) chronometer.getTag() - SystemClock.elapsedRealtime() / 1000;
                if (time > 0) {
                    chronometer.setText("（" + time + "）重新获取");
                } else {
                    chronometer.setText("重新获取");
                    chronometer.stop();
                    chronometer.setEnabled(true);
                }
            }
        });
        sendCheckCode.setText("获取验证码");

    }

    public void onLogin(View view) {
        String phone = username.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Utils.showToast(ModifyPhoneActivity.this, "请输入11位手机号码");
            phone = null;
            return;
        }
        if (!phone.startsWith("1") || phone.length() != 11) {
            Utils.showToast(ModifyPhoneActivity.this, "请输入正确的手机号码");
            phone = null;
            return;
        }
        if (TextUtils.isEmpty(checkCode.getText().toString().trim())) {
            Utils.showToast(ModifyPhoneActivity.this, "请输入验证码");
            return;
        }
        chagePhone();
    }

    // 提交修改信息
    private void login() {
        // 重新登录
        MyApplication.MySharedPreferences.saveIsLogin(false);
        startActivity(new Intent(this, LoginActivity.class));
        EventTypeString message = new EventTypeString();
        message.setMessage("Logout");
        EventBus.getDefault().post(message);
        finish();
    }

    public void OnFinish(View view) {
        finish();
    }

    // 发送验证码
    private void sendCheckCode() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("user_tel", username.getText().toString());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("验证码发送成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    Utils.showToast(ModifyPhoneActivity.this, dto.getTxt_message());
                    sendCheckCode.setTag(SystemClock.elapsedRealtime() / 1000 + 60);
                    sendCheckCode.setText("（60）重新获取");
                    sendCheckCode.start();
                    sendCheckCode.setEnabled(false);
                } else {
                    Utils.showToast(ModifyPhoneActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, ModifyPhoneActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "public/SendSMS.php");

    }

    private void chagePhone() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_usertel", username.getText().toString());
        ajaxParams.put("txt_code", checkCode.getText().toString());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("验证码发送成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    login();
                } else {
                    Utils.showToast(ModifyPhoneActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, ModifyPhoneActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/modifyUser.php");

    }
}
