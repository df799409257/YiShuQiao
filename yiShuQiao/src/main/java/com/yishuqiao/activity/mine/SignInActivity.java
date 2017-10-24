package com.yishuqiao.activity.mine;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-7 上午11:27:57
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：SignInActivity.java
 * <p>
 * 类说明：签到界面
 */
public class SignInActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;// 标题

    @Bind(R.id.imageView)
    ImageView imageView;// 签到图片

    @Bind(R.id.tv_bfb)
    TextView tv_bfb;// 签到百分比

    @Bind(R.id.tv_lianxv)
    TextView tv_lianxv;// 连续登录数

    @Bind(R.id.tv_louqian)
    TextView tv_louqian;// 漏签数

    @Bind(R.id.tv_data1)
    TextView tv_data1;// 签到开始时间

    @Bind(R.id.tv_data2)
    TextView tv_data2;// 签到间断时间

    @Bind(R.id.tv_data3)
    TextView tv_data3;// 最后签到时间

    @Bind(R.id.tv_lxqd)
    TextView tv_lxqd;// 连续签到数

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        tv_title.setText("签到");

        getDataFormService();

    }

    private void getDataFormService() {
        FinalHttp fh = new FinalHttp();
        fh.addHeader("user_token", "5e8901a33b650abd79099bd2ec428e64");// 配置http请求头
        fh.configRequestExecutionRetryCount(3);// 请求错误重试次数
        fh.configTimeout(5000);// 超时时间
        AjaxParams params = new AjaxParams();
        String http = "http://api.yishuqiao.cn:8083/user/signinfo";
        fh.post(http, params, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String t) {

                Log.e("签到结果", t);

                try {
                    JSONObject object = new JSONObject(t.toString());
                    if (object.optString("result").equals("true")) {
                        JSONObject dataobject = object.getJSONObject("data");
                        JSONArray array = dataobject.getJSONArray("workData");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.optJSONObject(i);

                            Log.e("zsy",
                                    "--结束 signend_date-- = "
                                            + data.optString("signend_date"));
                            Log.e("zsy",
                                    "--开始 signsta_date-- = "
                                            + data.optString("signsta_date"));
                            Log.e("zsy",
                                    "-- signup_date-- = "
                                            + data.optString("signup_date"));
                            Log.e("zsy",
                                    "--连续签到 conday-- = "
                                            + data.optString("conday"));

                            if (data.optString("signend_date").equals("")
                                    || data.optString("signend_date") == null) {
                                tv_data3.setText("0");
                            } else {
                                tv_data3.setText(timesdata(data
                                        .optString("signend_date")));
                            }

                            if (data.optString("signsta_date").equals("")
                                    || data.optString("signsta_date") == null) {
                                tv_data2.setText("0");
                            } else {
                                tv_data2.setText(timesdata(data
                                        .optString("signsta_date")));
                            }

                            if (data.optString("signup_date").equals("")) {
                                tv_data1.setText("0");
                            } else {
                                tv_data1.setText(timesdata(data
                                        .optString("signup_date")));
                            }

                            if (data.optString("conday").equals("")) {
                                tv_lxqd.setText("连续签到0天");
                            } else {
                                tv_lxqd.setText("连续签到"
                                        + data.optString("conday") + "天");
                            }

                            if (data.optString("conday").equals("")) {
                                tv_lianxv.setText("0");
                            } else {
                                tv_lianxv.setText(data.optString("conday"));
                            }

                            java.text.DecimalFormat df = new java.text.DecimalFormat(
                                    "#0.00");

                            if (!data.optString("signsta_date").equals("")
                                    && !data.optString("signup_date")
                                    .equals("")) {
                                int louqian = data.optInt("signsta_date")
                                        - data.optInt("signup_date");
                                tv_louqian.setText("漏签" + times(louqian + "")
                                        + "天");
                            }
                            if (!data.optString("down_user").equals("")
                                    && !data.optString("all_user").equals("")) {
                                double bfb = data.optDouble("down_user")
                                        / data.optDouble("all_user");
                                Log.e("zsy", "bfb=" + bfb);
                                tv_bfb.setText(df.format(bfb * 100) + "%");
                            }

                            String end = data.optString("signend_date");
                            String start = data.optString("signsta_date");
                            String up = data.optString("signup_date");

                            if (end.equals("")) {
                                imageView
                                        .setBackgroundResource(R.drawable.xian4);
                                tv_data3.setVisibility(View.INVISIBLE);
                            } else if (up.equals("")) {
                                imageView
                                        .setBackgroundResource(R.drawable.xian2);
                                tv_data3.setVisibility(View.INVISIBLE);
                                tv_data2.setVisibility(View.INVISIBLE);
                            } else if (start.equals("")) {
                                imageView
                                        .setBackgroundResource(R.drawable.xian0);
                                // tv_data3.setVisibility(View.INVISIBLE);
                            } else {
                                imageView
                                        .setBackgroundResource(R.drawable.xian3);
                            }
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "网络连接失败,请重试",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(t);
            }
        });

    }

    // 点击签到
    @OnClick(R.id.tv_qiandao)
    public void signIn(TextView signIn) {

        FinalHttp fh = new FinalHttp();
        fh.addHeader("user_token", "5e8901a33b650abd79099bd2ec428e64");// 配置http请求头
        fh.configRequestExecutionRetryCount(3);// 请求错误重试次数
        fh.configTimeout(5000);// 超时时间
        AjaxParams params = new AjaxParams();
        String http = "http://api.yishuqiao.cn:8083/user/signin";
        fh.post(http, params, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String t) {

                try {
                    JSONObject object = new JSONObject(t.toString());
                    if (object.optString("result").equals("true")) {
                        Toast.makeText(SignInActivity.this, "签到成功",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignInActivity.this, "网络连接失败,请重试",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(t);
            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    public static String timesdata(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    @SuppressLint("SimpleDateFormat")
    public static String times(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("dd");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    public void onFinish(View view) {
        finish();
    }
}
