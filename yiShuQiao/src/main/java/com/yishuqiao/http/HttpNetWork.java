package com.yishuqiao.http;

import com.yishuqiao.utils.LogUtil;
import com.yishuqiao.utils.Utils;

import android.content.Context;
import android.text.TextUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class HttpNetWork {

    private HttpRusult r;
    private Context context;

    public HttpNetWork(HttpRusult result, Context context) {
        this.context = context;
        this.r = result;
    }

    public void postHttp(AjaxParams params, String url) {
        FinalHttp finalHttp = new FinalHttp();
        finalHttp.configRequestExecutionRetryCount(3);// 请求错误重试次数
        finalHttp.configTimeout(9000);// 超时时间
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                r.start();
                super.onStart();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                // TODO Auto-generated method stub
                r.error();
                if (!TextUtils.isEmpty(strMsg)) {
                    LogUtil.showLargeLog(strMsg, 3000, context.getClass().getSimpleName());
                }
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(t.toString())) {
                    LogUtil.showLargeLog(t.toString(), 3000, context.getClass().getSimpleName());
                }
                if (Utils.isGson(t + "")) {
                    r.succ(t + "");
                } else {
                    r.error();
                }

            }
        });
    }

}
