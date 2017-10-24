package com.yishuqiao.view.looperview;

import com.android.volley.toolbox.ImageLoader;
import com.yishuqiao.R;
import com.yishuqiao.utils.DownLoadImage;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements Holder<String> {

    private ImageView imageView;
    private DownLoadImage downLoadImage;

    @Override
    public View createView(Context context) {
        // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        imageView.setImageResource(R.drawable.icon_empty);
        downLoadImage = new DownLoadImage(context);
        downLoadImage.DisplayImage(data, imageView);
    }
}
