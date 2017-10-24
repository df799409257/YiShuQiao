package com.yishuqiao.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.utils.ViewColor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 更新界面
 *
 * @author Administrator
 */
public class UpDateActivity extends BaseActivity implements OnClickListener {

    private TextView content;
    private Button bt_cancle, bt_ok;

    private long mExitTime;

    public static String DOWNLOADURL = "url";
    public static String CONTENT = "content";

    private ProgressBar pb;
    int fileSize;
    int downLoadFileSize;
    private String filename;
    private String filePath = null;
    @SuppressLint({"HandlerLeak", "ShowToast"})
    private Handler handler = new Handler() {

        @SuppressWarnings("unused")
        @Override
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        pb.setMax(fileSize);
                        System.out.println("文件大小：" + fileSize);
                    case 1:
                        pb.setProgress(downLoadFileSize);
                        int result = downLoadFileSize * 100 / fileSize;
                        break;
                    case 2:
                        bt_ok.setEnabled(true);
                        Toast.makeText(UpDateActivity.this, "文件下载完成", Toast.LENGTH_SHORT).show();
                        update(UpDateActivity.this);
                        finish();
                        break;
                    case -1:
                        bt_ok.setEnabled(true);
                        String error = msg.getData().getString("error");
                        Toast.makeText(UpDateActivity.this, error, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);

        // 设置沉浸式状态栏的颜色
        ViewColor.setColor(this, Color.rgb(251, 72, 71));
        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        initView();

    }

    private void initView() {

        content = (TextView) findViewById(R.id.content);
        bt_cancle = (Button) findViewById(R.id.bt_cancle);
        bt_cancle.setOnClickListener(this);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(this);

        pb = (ProgressBar) findViewById(R.id.down_pb);

        if (getIntent().hasExtra(DOWNLOADURL)) {
            filePath = getIntent().getStringExtra(DOWNLOADURL);
            Log.e("filePath1111", filePath);
        }
        Log.e("filePath2222", getIntent().getStringExtra(DOWNLOADURL));
        if (getIntent().hasExtra(CONTENT)) {
            content.setText(getIntent().getStringExtra(CONTENT));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_cancle:
                Utils.showToast(UpDateActivity.this, "对不起,有新版本建议您升级。");
                break;
            case R.id.bt_ok:
                bt_ok.setEnabled(false);
                new Thread() {

                    public void run() {
                        try {
                            System.out.println("下载到内存");
                            down_file(filePath, Environment.getExternalStorageDirectory().toString() + "/");
                            // 下载文件，参数：n第一个URL，第二个存放路径
                        }
                        // catch (ClientProtocolException e) {
                        // // TODO Auto-generated catch block
                        // e.printStackTrace();
                        // }
                        catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                MyApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("resource")
    public void down_file(String url, String path) throws IOException {
        // 下载函数
        filename = url.substring(url.lastIndexOf("/") + 1);
        System.out.println("下载的文件名是：" + filename);
        System.out.println("下载的链接是：" + url);
        // 获取文件名
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        this.fileSize = conn.getContentLength();// 根据响应获取文件大小
        System.out.println("下载文件大小是：" + this.fileSize);
        if (this.fileSize <= 0)
            throw new RuntimeException("无法获知文件大小 ");
        if (is == null)
            throw new RuntimeException("stream is null");
        FileOutputStream fos = new FileOutputStream(path + filename);
        // 把数据存入路径+文件名
        byte buf[] = new byte[1024];
        downLoadFileSize = 0;
        sendMsg(0);
        do {
            // 循环读取
            int numread = is.read(buf);
            if (numread == -1) {
                break;
            }
            fos.write(buf, 0, numread);
            downLoadFileSize += numread;

            sendMsg(1);// 更新进度条
        } while (true);
        sendMsg(2);// 通知下载完成
        try {
            is.close();
        } catch (Exception ex) {
            Log.e("tag", "error: " + ex.getMessage(), ex);
            bt_ok.setEnabled(true);
        }

    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    /**
     * 安装apk,如果下载到内存中，先赋予当前文件夹，安装程序权限
     *
     * @param context
     */
    void update(Context context) {

        System.out.println("下载完毕");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/",
                filePath.substring(filePath.lastIndexOf("/") + 1))), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
