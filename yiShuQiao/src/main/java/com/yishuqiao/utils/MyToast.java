package com.yishuqiao.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-11 上午11:38:08
 * <p>
 * 项目名称：YueGuiF
 * <p>
 * 文件名称：MyToast.java
 * <p>
 * 类说明：Toast管理类 在界面不可见时可关闭Toast
 */
@SuppressLint("ShowToast")
public class MyToast {

    private Context mContext;
    private Toast mToast;

    /**
     * 作者：admin
     * <p>
     * 创建时间：2017-7-11 上午11:38:08
     * <p>
     * 项目名称：YueGuiF
     * <p>
     * 文件名称：MyToast.java
     * <p>
     * 类说明：Toast管理类 在界面不可见时可关闭Toast
     */
    public MyToast(Context context) {

        mContext = context;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        // mToast.setGravity(17, 0, -30);// 居中显示 可控制toast显示位置
    }

    public void show(int resId) {
        show(mContext.getText(resId));
    }

    public void show(CharSequence s) {
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(s);
        mToast.show();
    }

    public void cancel() {
        mToast.cancel();
    }

}
