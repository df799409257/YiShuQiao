package com.yishuqiao.activity.mine;

import net.tsz.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.LoginDTO;
import com.yishuqiao.dto.UploadDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.DecodeUri;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.RoundImageView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName PersonalInfoActivity
 * @Description TODO(个人中心界面)
 * @Date 2017年7月31日 上午11:22:03
 */
public class PersonalInfoActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView title;

    @Bind(R.id.modifyAvatarBtn)
    RelativeLayout modifyAvatarBtn = null;// 修改头像

    @Bind(R.id.modifyNickNameBtn)
    LinearLayout modifyNickNameBtn = null;// 修改昵称

    @Bind(R.id.modifyGenderBtn)
    RelativeLayout modifyGenderBtn = null;// 修改性别

    @Bind(R.id.modifyPhonesBtn)
    LinearLayout modifyPhonesBtn = null;// 修改手机号

    @Bind(R.id.modifyPassword)
    LinearLayout modifyPassword = null;// 修改密码

    @Bind(R.id.avatar)
    RoundImageView avatar = null;

    @Bind(R.id.nickNameTv)
    TextView nickNameTv = null;

    @Bind(R.id.genderTv)
    TextView genderTv = null;

    @Bind(R.id.phoneNumTv)
    TextView phoneNumTv = null;

    private int MODIFY_AVATAR_REQUESTCODE = 1;// 修改头像
    private int MODIFY_GENDER_REQUESTCODE = 2;// 修改性别

    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personalinfo);

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

        title.setText("个人信息");

        initData();

    }

    // 获取用户信息
    private void initData() {

        String avatarUrl = MyApplication.MySharedPreferences.readUserPic();// 头像地址
        String nickName = MyApplication.MySharedPreferences.readUsername();// 用户名
        String gender = MyApplication.MySharedPreferences.readGender();// 用户性别
        String phone = MyApplication.MySharedPreferences.readTel();// 用户手机号
        Log.e("zsy", "phone=" + phone);
        // 显示用户信息
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(this).load(avatarUrl).error(R.drawable.default_user_icon).fit().into(avatar);
        }
        nickNameTv.setText(nickName);
        genderTv.setText(gender);
        phoneNumTv.setText(Utils.chageStar(phone));
    }

    // 修改头像
    @OnClick(R.id.modifyAvatarBtn)
    public void changeHeadImg(RelativeLayout relativeLayout) {
        Intent intent = new Intent(PersonalInfoActivity.this, ModifyAvatarPopActivity.class);
        startActivityForResult(intent, MODIFY_AVATAR_REQUESTCODE);
    }

    // 修改昵称
    @OnClick(R.id.modifyNickNameBtn)
    public void changeUserName(LinearLayout linearLayout) {
        showTipsDialog(R.layout.modify_nick_dialog_layout);
    }

    // 修改性别
    @OnClick(R.id.modifyGenderBtn)
    public void changeGender(RelativeLayout relativeLayout) {
        Intent intent = new Intent(PersonalInfoActivity.this, ModifyGenderPopActivity.class);
        String gender = MyApplication.MySharedPreferences.readGender();
        intent.putExtra("gender", gender);
        startActivityForResult(intent, MODIFY_GENDER_REQUESTCODE);
    }

    // 修改手机号码
    @OnClick(R.id.modifyPhonesBtn)
    public void changePhone(LinearLayout linearLayout) {
        Intent intent = new Intent(PersonalInfoActivity.this, ModifyPhoneActivity.class);
        startActivity(intent);
    }

    // 修改密码
    @OnClick(R.id.modifyPassword)
    public void changePassword(LinearLayout linearLayout) {
        Intent intent = new Intent(PersonalInfoActivity.this, ResetpasswordActivity.class);
        startActivity(intent);
    }

    private void showTipsDialog(final int layoutId) {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(layoutId, null);
        final EditText nickName = (EditText) view.findViewById(R.id.userName);
        nickName.setText(MyApplication.MySharedPreferences.readUsername());
        view.findViewById(R.id.cancelTV).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.confirmTV).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nickName.getText().toString())) {
                    Utils.showToast(PersonalInfoActivity.this, "请填写新昵称");
                    return;
                }
                SaveData(nickName.getText().toString(), "2");
                // submitforNick(nickName.getText().toString());
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.del).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                nickName.setText("");
            }
        });
        dialog.setContentView(view);

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (MODIFY_AVATAR_REQUESTCODE == requestCode && resultCode == RESULT_OK) {
            if (data != null) {
                SavePhotoUrl(data.getData());
                // submitForAvatar(data.getData());
            }

        } else if (MODIFY_GENDER_REQUESTCODE == requestCode && resultCode == RESULT_OK) {
            String gender = (String) data.getStringExtra("gender");
            if (gender != null && !"".equals(gender)) {
                SaveData(gender, "3");
            }

        }
    }

    // 向服务器提交数据(修改昵称)
    private void submitforNick(final String nickName) {

        MyApplication.MySharedPreferences.saveUsername(nickName);
        nickNameTv.setText(nickName);

        EventTypeString message = new EventTypeString();
        message.setMessage("refresh");
        EventBus.getDefault().post(message);
    }

    // 向服务器提交数据(修改头像)
    @SuppressLint("ShowToast")
    private void submitForAvatar(final Uri uri, String key) {

        Bitmap bitmap = DecodeUri.decodeUri(PersonalInfoActivity.this, uri, 65, 65);
        if (bitmap != null) {
            avatar.setImageBitmap(bitmap);
        }

        EventTypeString message = new EventTypeString();
        message.setMessage("refresh");
        EventBus.getDefault().post(message);

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("find_type", "3");
        ajaxParams.put("find_detail", "");
        ajaxParams.put("find_img", key);
        Log.e("key = ", key);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(PersonalInfoActivity.this, dto.getTxt_message());

                } else {
                    Utils.showToast(PersonalInfoActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
                Toast.makeText(PersonalInfoActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);

    }

    // 向服务器提交数据(修改性别)
    private void submitforGender(final String gender) {

        MyApplication.MySharedPreferences.saveGender(gender);
        genderTv.setText(gender);

        EventTypeString message = new EventTypeString();
        message.setMessage("refresh");
        EventBus.getDefault().post(message);
    }

    public void onFinish(View view) {
        finish();
    }

    /**
     * flag用来区分是修改什么 2修改昵称 3修改性别
     *
     * @param str
     * @param flag
     */
    public void SaveData(final String str, final String flag) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", flag);
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        if (flag.equals("2")) {
            ajaxParams.put("txt_nickname", str);
        } else if (flag.equals("3")) {
            ajaxParams.put("txt_usersex", str);
        }
        Log.e("zsy", "str=" + str);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                LoginDTO dto = new LoginDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("登录成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, LoginDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(PersonalInfoActivity.this, "修改成功");

                    if (flag.equals("2")) {
                        submitforNick(str);
                    } else if (flag.equals("3")) {
                        submitforGender(str);
                    }

                } else {
                    Utils.showToast(PersonalInfoActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, PersonalInfoActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/modifyUser.php");

    }

    /**
     * 上传头像
     *
     * @param str
     * @param flag
     */
    public void SavePhotoUrl(final Uri uri) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_cate", "1");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    MyApplication.getConfiguration().put(Utils.getRealFilePath(PersonalInfoActivity.this, uri),
                            Utils.imgName(), dto.getUptoken(), new UpCompletionHandler() {

                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject res) {

                                    Log.e("key", "key = " + key);

                                    // info.error中包含了错误信息，可打印调试
                                    // 上传成功后将key值上传到自己的服务器
                                    if (info.isOK()) {
                                        submitForAvatar(uri, key);
                                    } else {
                                        Toast.makeText(PersonalInfoActivity.this, "服务器异常,头像更换失败", 300).show();
                                    }
                                    Log.e("zsy", key + ",\r\n " + info + ",\r\n " + res);

                                }
                            }, null);

                } else {
                    Utils.showToast(PersonalInfoActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, PersonalInfoActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "upload/upload.php");

    }

}
