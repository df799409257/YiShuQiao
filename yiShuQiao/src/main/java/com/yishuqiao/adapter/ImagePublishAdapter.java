package com.yishuqiao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yishuqiao.R;
import com.yishuqiao.dto.ImageItem;
import com.yishuqiao.utils.CustomConstants;
import com.yishuqiao.utils.ImageDisplayer;

public class ImagePublishAdapter extends BaseAdapter {

    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private Context mContext;

    public ImagePublishAdapter(Context context, List<ImageItem> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public int getCount() {
        // 多返回一个用于展示添加图�?
        if (mDataList == null) {
            return 1;
        } else if (mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return CustomConstants.MAX_IMAGE_SIZE;
        } else {
            return mDataList.size() + 1;
        }
    }

    public Object getItem(int position) {
        if (mDataList != null && mDataList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return mDataList.get(position);
        } else if (mDataList == null || position - 1 < 0 || position > mDataList.size()) {
            return null;
        } else {
            return mDataList.get(position - 1);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        // �?��Item展示不满�?��，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问�?
        convertView = View.inflate(mContext, R.layout.item_publish, null);
        ImageView imageIv = (ImageView) convertView.findViewById(R.id.item_grid_image);

        if (isShowAddItem(position)) {
            imageIv.setImageResource(R.drawable.btn_add_pic);
            imageIv.setBackgroundResource(R.drawable.rectangle_frame);
        } else {
            final ImageItem item = mDataList.get(position);
            ImageDisplayer.getInstance(mContext).displayBmp(imageIv, item.thumbnailPath, item.sourcePath);
            Log.e("图片路径", "thumbnailPath = " + item.thumbnailPath + "     sourcePath = " + item.sourcePath);
        }

        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = mDataList == null ? 0 : mDataList.size();
        return position == size;
    }

}
