package com.yishuqiao.jpush;

import android.content.Context;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName JPushMsgReceiver
 * @Description TODO( <!-- Required since 3.0.7 --> <!-- 新的tag/alias接口结果返回需要开发者配置一个自定的广播 --> <!--
 * 该广播需要继承JPush提供的JPushMessageReceiver类, 并如下新增一个 Intent-Filter -->)
 * @Date 2017年8月2日 下午2:22:34
 */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onAliasOperatorResult(Context arg0, JPushMessage arg1) {

        super.onAliasOperatorResult(arg0, arg1);
    }

    @Override
    public void onCheckTagOperatorResult(Context arg0, JPushMessage arg1) {

        super.onCheckTagOperatorResult(arg0, arg1);
    }

    @Override
    public void onTagOperatorResult(Context arg0, JPushMessage arg1) {

        super.onTagOperatorResult(arg0, arg1);
    }

}
