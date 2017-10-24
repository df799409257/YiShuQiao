package com.yishuqiao.view.looperview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * 本地图片Holder例子
 */
public class LocalImageHolderView implements Holder<Integer> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        // 把图片按照指定的大小在View中显示，拉伸显示图片，不保持原比例，全部显示图片填满View.关键字：不保持比例，拉伸显示全图，填满ImageView
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, Integer data) {
        imageView.setImageResource(data);
    }
}
