package com.yishuqiao.fragment;

import org.greenrobot.eventbus.EventBus;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.LoginDTO;
import com.yishuqiao.dto.PublicDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import net.tsz.afinal.http.AjaxParams;

/**
 * 作者：admin 创建时间：2017-7-5 上午10:51:57 项目名称：YiShuQiao 文件名称：FastLoginFragment.java 类说明：快速登录
 */
public class FastLoginFragment extends BaseFragment {

    private Chronometer sendCheckCode = null;
    private EditText username = null;
    private EditText checkCode = null;

    private TextView tv_fastlogin_login = null;

    private Activity mActivity;

    // private MultiStateView mStateView;

    private CustomProgressDialog progressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        View view = View.inflate(mActivity, R.layout.fragment_fast_login, null);

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(mActivity);
        }

        username = (EditText) view.findViewById(R.id.et_fastlogin_userName);
        checkCode = (EditText) view.findViewById(R.id.et_fastlonig_checkCode);
        sendCheckCode = (Chronometer) view.findViewById(R.id.ch_fastlogin_sendCheckCode);
        tv_fastlogin_login = (TextView) view.findViewById(R.id.tv_fastlogin_login);
        // 登录按钮设置点击事件
        tv_fastlogin_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 点击登录
                onLogin(view);
            }
        });
        sendCheckCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = username.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(mActivity, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
                    phone = null;
                    return;
                }
                if (!phone.startsWith("1") || phone.length() != 11) {
                    Toast.makeText(mActivity, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    phone = null;
                    return;
                }

                // 先影藏输入法
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

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

        return view;
    }

    // 发送验证码
    private void sendCheckCode() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("user_tel", username.getText().toString());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("验证码发送成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(getActivity(), dto.getTxt_message());
                    sendCheckCode.setTag(SystemClock.elapsedRealtime() / 1000 + 60);
                    sendCheckCode.setText("（60）重新获取");
                    sendCheckCode.start();
                    sendCheckCode.setEnabled(false);

                    username.setFocusable(false);
                    checkCode.setFocusable(true);// 获取焦点并弹出键盘
                    checkCode.setFocusableInTouchMode(true);
                    checkCode.requestFocus();
                    getActivity().getWindow()
                            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                } else {
                    Utils.showToast(getActivity(), dto.getTxt_message());
                }
            }

            @Override
            public void start() {
                progressDialog.show();
            }

            @Override
            public void error() {
                progressDialog.dismiss();
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "public/SendSMS.php");

    }

    public void onLogin(View view) {
        String phone = username.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(mActivity, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        if (!phone.startsWith("1") || phone.length() != 11) {
            Toast.makeText(mActivity, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        if (TextUtils.isEmpty(checkCode.getText().toString().trim())) {
            Toast.makeText(mActivity, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        login();
    }

    // 登录
    private void login() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "2");
        ajaxParams.put("txt_password", checkCode.getText().toString());
        ajaxParams.put("txt_usernum", username.getText().toString());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                LoginDTO dto = new LoginDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("登录成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, LoginDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    MyApplication.MySharedPreferences.saveIsLogin(true);
                    MyApplication.MySharedPreferences.saveUserid(dto.getUserInfo().getTxt_userid());
                    MyApplication.MySharedPreferences.saveToken(dto.getUserInfo().getTxt_usertoken());
                    if (null != dto.getUserInfo().getTxt_user_push()
                            && (dto.getUserInfo().getTxt_user_push()).equals("0")) {
                        MyApplication.MySharedPreferences.saveIsPush(false);
                    } else if (null != dto.getUserInfo().getTxt_user_push()
                            && (dto.getUserInfo().getTxt_user_push()).equals("1")) {
                        MyApplication.MySharedPreferences.saveIsPush(true);
                    }
                    MyApplication.MySharedPreferences.saveGender(dto.getUserInfo().getTxt_user_sex());
                    MyApplication.MySharedPreferences.saveTel(dto.getUserInfo().getTxt_user_tel());
                    MyApplication.MySharedPreferences.saveUsername(dto.getUserInfo().getTxt_nickname());
                    MyApplication.MySharedPreferences.saveUserid(dto.getUserInfo().getTxt_userid());
                    MyApplication.MySharedPreferences.saveRegtime(dto.getUserInfo().getTxt_txt_regtime());
                    MyApplication.MySharedPreferences.savePoints(dto.getUserInfo().getTxt_user_points());
                    MyApplication.MySharedPreferences.saveUserPic(dto.getUserInfo().getTxt_user_pic());

                    Utils.showToast(getActivity(), dto.getTxt_message());

                    // 极光推送相关
                    JPushInterface.setAlias(getActivity(), MyApplication.MySharedPreferences.readUserid(), null);

                    EventTypeString message = new EventTypeString();
                    message.setMessage("login");
                    EventBus.getDefault().post(message);

                    mActivity.finish();

                } else {
                    Utils.showToast(getActivity(), dto.getTxt_message());
                }
            }

            @Override
            public void start() {
                progressDialog.show();
            }

            @Override
            public void error() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }, getActivity());
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/login.php");

    }

}
