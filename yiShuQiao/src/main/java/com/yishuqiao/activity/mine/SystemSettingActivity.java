package com.yishuqiao.activity.mine;

import java.io.File;
import java.text.DecimalFormat;

import net.tsz.afinal.http.AjaxParams;

import org.greenrobot.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
import com.yishuqiao.utils.FileCache;
import com.yishuqiao.utils.MemoryCache;
import com.yishuqiao.utils.Utils;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName SystemSettingActivity
 * @Description TODO(设置界面)
 * @Date 2017年8月3日 下午6:33:56
 */
public class SystemSettingActivity extends BaseActivity {

    private Handler handler = null;//
    private TextView cache_size = null;
    private TextView tv_title = null;
    private TextView tv_switch = null;
    private TextView onLogout = null;

    private boolean flag = true;

    private String pushState = "1";

    private int cacheState = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemsetting);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();

    }

    @SuppressLint("HandlerLeak")
    private void initView() {

        cache_size = (TextView) findViewById(R.id.cache_size);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("系统设置");
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        onLogout = (TextView) findViewById(R.id.onLogout);

        if (MyApplication.MySharedPreferences.readIsPush()) {
            flag = false;
            tv_switch.setText("开");
            pushState = "0";
        } else {
            flag = true;
            tv_switch.setText("关");
            pushState = "1";
        }

        onLogout.setVisibility(MyApplication.MySharedPreferences.readIsLogin() ? View.VISIBLE : View.GONE);
        onLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                outLogin();
            }
        });

        handler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        cache_size.setText(msg.obj.toString());
                        if ((msg.obj.toString()).equals("0.0MB")) {
                            cacheState = 0;
                        } else {
                            cacheState = 1;
                        }
                        break;
                    case 1:
                        Utils.showToast(SystemSettingActivity.this, "清理完毕");
                        break;

                }
            }

            ;
        };

        cacheDataSize();

    }

    // 点击切换开关
    public void onSwitch(View view) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "6");
        ajaxParams.put("txt_ispush", pushState);
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("操作成功");
                    dto.setTxt_message("操作成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(SystemSettingActivity.this, dto.getTip());

                    if (flag) {
                        tv_switch.setText("开");
                        flag = false;
                        pushState = "0";
                        MyApplication.MySharedPreferences.saveIsPush(true);
                    } else {
                        tv_switch.setText("关");
                        flag = true;
                        pushState = "1";
                        MyApplication.MySharedPreferences.saveIsPush(false);
                    }

                } else {
                    Utils.showToast(SystemSettingActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, SystemSettingActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.ModifyUser);

    }

    // 清除缓存
    public void delChcheDialog(View view) {
        if (cacheState == 0) {
            Utils.showToast(SystemSettingActivity.this, "还没有缓存");
        } else if (cacheState == 1) {

            new AlertDialog.Builder(SystemSettingActivity.this).setTitle(null).setMessage("确定要清除缓存吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            delCacheData();
                        }

                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    }

    // 意见反馈
    public void startFeedBack(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    // 商务合作
    public void startBusiness(View view) {
        Intent intent = new Intent(this, BusinessActivity.class);
        startActivity(intent);
    }

    // 退出登录
    public void onLogout() {

        MyApplication.MySharedPreferences.saveIsLogin(false);
        EventTypeString message = new EventTypeString();
        message.setMessage("Logout");
        EventBus.getDefault().post(message);
        finish();

    }

    // 删除缓存数据
    private void delCacheData() {

        new Thread() {

            public void run() {

                FileCache fileCache = new FileCache(getApplicationContext());
                fileCache.clear();
                MemoryCache memoryCache = new MemoryCache();
                memoryCache.clear();

                ((MyApplication) SystemSettingActivity.this.getApplication()).DelSerFile();
                ((MyApplication) SystemSettingActivity.this.getApplication()).DelImgCacheDataFile();

                cacheDataSize();
                handler.sendEmptyMessage(1);
            }
        }.start();

    }

    // 计算缓存大小
    private void cacheDataSize() {

        new Thread() {

            public void run() {
                int MB = 1024 * 1024;
                double cacheSize = 0;

                try {

                    File serDir = getApplicationContext().getFilesDir();
                    File[] serFiles = serDir.listFiles();
                    for (int i = 0; i < serFiles.length; i++) {
                        if (serFiles[i].getName().contains(".ser")) {
                            cacheSize += serFiles[i].length();
                        }
                    }

                    File imgCacheDataPath_picasso = new File(getApplicationContext().getCacheDir(), "picasso-cache");
                    File[] imgCacheFiles_picasso = imgCacheDataPath_picasso.listFiles();
                    for (int i = 0; i < imgCacheFiles_picasso.length; i++) {
                        cacheSize += imgCacheFiles_picasso[i].length();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // String size = Math.round((cacheSize / MB) * 10) / 10.0 +
                // "MB";
                DecimalFormat df = new DecimalFormat("0.0");
                String size = df.format(cacheSize / MB) + "MB";

                Message message = new Message();
                message.what = 0;
                message.obj = size;
                handler.sendMessage(message);
            }
        }.start();

    }

    public void onFinish(View view) {
        finish();
    }

    /**
     * 退出
     *
     * @param id
     */
    private void outLogin() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                PublicDTO dto = new PublicDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("退出成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, PublicDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {
                    onLogout();
                } else {
                    Utils.showToast(SystemSettingActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
            }

            @Override
            public void error() {
            }

        }, SystemSettingActivity.this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Urlmy + "user/login.php");

    }
}
