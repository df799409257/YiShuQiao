package com.yishuqiao.view.looperview;

import android.content.Context;
import android.view.View;

/**
 * 任何指定的对象
 */
public interface Holder<T> {
    View createView(Context context);

    void UpdateUI(Context context, int position, T data);
}