package com.yishuqiao.fragment;

import org.greenrobot.eventbus.EventBus;

import com.google.gson.Gson;
import com.yishuqiao.R;
import com.yishuqiao.activity.mine.ResetpasswordActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.LoginDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import net.tsz.afinal.http.AjaxParams;

/**
 * ���ߣ�admin ����ʱ�䣺2017-7-5 ����11:00:36 ��Ŀ���ƣ�YiShuQiao �ļ����ƣ�CommonLoginFragment.java ��˵������ͨ��¼����
 */
public class CommonLoginFragment extends BaseFragment {

    private Activity mActivity;

    @Bind(R.id.et_commonlogin_userName)
    EditText etCommonloginUserName;// �û��ֻ���

    @Bind(R.id.et_commonlogin_checkCode)
    EditText etCommonloginCheckCode;// �û�����

    // @Bind(R.id.stateview)
    // MultiStateView stateview;

    private CustomProgressDialog progressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        View view = View.inflate(mActivity, R.layout.fragment_common_login, null);

        ButterKnife.bind(this, view);

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(getActivity());
        }

        return view;

    }

    // �����¼
    @OnClick(R.id.resetPsd)
    public void resetPsd(TextView resetpsd) {
        startActivity(new Intent(mActivity, ResetpasswordActivity.class));
        mActivity.finish();
    }

    // �����¼
    @OnClick(R.id.tv_commonlogin_login)
    public void onLogin(TextView login) {
        String phone = etCommonloginUserName.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(mActivity, "������11λ�ֻ�����", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        if (!phone.startsWith("1") || phone.length() != 11) {
            Toast.makeText(mActivity, "��������ȷ���ֻ�����", Toast.LENGTH_SHORT).show();
            phone = null;
            return;
        }
        if (TextUtils.isEmpty(etCommonloginCheckCode.getText().toString().trim())) {
            Toast.makeText(mActivity, "����������", Toast.LENGTH_SHORT).show();

            return;
        }
        login();
    }

    // ��¼
    private void login() {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_password", etCommonloginCheckCode.getText().toString());
        ajaxParams.put("txt_usernum", etCommonloginUserName.getText().toString());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                LoginDTO dto = new LoginDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("��¼�ɹ�");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, LoginDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(getActivity(), dto.getTxt_message());

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

                    // �����������
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
