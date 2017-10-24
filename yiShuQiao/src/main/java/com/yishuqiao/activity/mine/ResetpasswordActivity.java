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
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import net.tsz.afinal.http.AjaxParams;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ResetpasswordActivity
 * @Description TODO(重置密码界面)
 * @Date 2017年7月31日 下午4:46:23
 */
public class ResetpasswordActivity extends BaseActivity {

    private boolean checkPsd = false;
    private boolean checkAgainPsd = false;

    @Bind(R.id.et_phone)
    EditText et_phone;// 手机号码

    @Bind(R.id.et_checkCode)
    EditText et_checkCode;// 验证码

    @Bind(R.id.iv_check1)
    ImageView iv_check1;// 新密码查看

    @Bind(R.id.iv_check2)
    ImageView iv_check2;// 确认密码查看

    @Bind(R.id.newPsd)
    EditText newPsd;// 新密码

    @Bind(R.id.againPsd)
    EditText againPsd;// 确认密码

    @Bind(R.id.sendCheckCode)
    Chronometer sendCheckCode;// 发送验证码

    private String phone, checkCode, newpsd, againpsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resetpassword);

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

        if (!TextUtils.isEmpty(MyApplication.MySharedPreferences.readTel())) {
            et_phone.setText(MyApplication.MySharedPreferences.readTel());
            et_phone.setEnabled(false);
        }

        iv_check1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(newPsd.length() == 0)) {
                    if (checkPsd) {
                        newPsd.setInputType(0x81);// 可见
                        iv_check1.setImageResource(R.drawable.password_on);
                        checkPsd = false;
                    } else {
                        newPsd.setInputType(0x90);// 不可见
                        iv_check1.setImageResource(R.drawable.password_off);
                        checkPsd = true;
                    }
                } else {
                    Utils.showToast(ResetpasswordActivity.this, "请输入密码");
                }

            }
        });

        iv_check2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(againPsd.length() == 0)) {
                    if (checkAgainPsd) {
                        againPsd.setInputType(0x81);// 可见
                        iv_check2.setImageResource(R.drawable.password_on);
                        checkAgainPsd = false;
                    } else {
                        againPsd.setInputType(0x90);// 不可见
                        iv_check2.setImageResource(R.drawable.password_off);
                        checkAgainPsd = true;
                    }
                } else {
                    Utils.showToast(ResetpasswordActivity.this, "请输入确认密码");
                }

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

    // 发送验证码
    @OnClick(R.id.sendCheckCode)
    public void sendCheckCode(Chronometer submit) {
        String phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ResetpasswordActivity.this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        if (!phone.startsWith("1") || phone.length() != 11) {
            Toast.makeText(ResetpasswordActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        sendCheckCode();
    }

    // 发送验证码
    private void sendCheckCode() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("user_tel", et_phone.getText().toString());
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

                    Utils.showToast(ResetpasswordActivity.this, dto.getTxt_message());

                    sendCheckCode.setTag(SystemClock.elapsedRealtime() / 1000 + 60);
                    sendCheckCode.setText("（60）重新获取");
                    sendCheckCode.start();
                    sendCheckCode.setEnabled(false);

                } else {
                    Utils.showToast(ResetpasswordActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "public/SendSMS.php");

    }

    // 提交修改
    @OnClick(R.id.submit)
    public void submit(TextView submit) {

        phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ResetpasswordActivity.this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            et_phone.setText("");
            return;
        }
        if (!phone.startsWith("1") || phone.length() != 11) {
            Toast.makeText(ResetpasswordActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            et_phone.setText("");
            return;
        }
        checkCode = et_checkCode.getText().toString();
        if (TextUtils.isEmpty(checkCode)) {
            Toast.makeText(ResetpasswordActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            checkCode = null;
            et_checkCode.setText("");
            return;
        }
        newpsd = newPsd.getText().toString();
        if (TextUtils.isEmpty(newpsd)) {
            Toast.makeText(ResetpasswordActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
            newpsd = null;
            newPsd.setText("");
            return;
        }
        againpsd = againPsd.getText().toString();
        if (TextUtils.isEmpty(againpsd)) {
            Toast.makeText(ResetpasswordActivity.this, "请输入确认密码", Toast.LENGTH_SHORT).show();
            againpsd = null;
            againPsd.setText("");
            return;
        }
        if (newpsd.equals(againpsd)) {
            commit();
        } else {
            Toast.makeText(ResetpasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            againpsd = null;
            againPsd.setText("");
            return;
        }
    }

    // 请求提交
    private void commit() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "5");
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_code", checkCode);
        ajaxParams.put("txt_userpwd", againpsd);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("修改成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(ResetpasswordActivity.this, dto.getTip());

                    MyApplication.MySharedPreferences.saveIsLogin(false);

                    EventTypeString message = new EventTypeString();
                    message.setMessage("Logout");
                    EventBus.getDefault().post(message);

                    startActivity(new Intent(ResetpasswordActivity.this, LoginActivity.class));
                    finish();

                } else {
                    Utils.showToast(ResetpasswordActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {

            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/modifyUser.php");

    }

    public void onFinish(View view) {
        finish();
    }

}
