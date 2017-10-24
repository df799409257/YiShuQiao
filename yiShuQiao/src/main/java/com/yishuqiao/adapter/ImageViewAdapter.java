package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.dto.NewsDTO.NewInfor.ItemImageUrlList;
import com.yishuqiao.utils.DownLoadImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageViewAdapter extends BaseAdapter {

    List<ItemImageUrlList> list;
    LayoutInflater inflater;
    Context context;
    DownLoadImage donDownLoadImage;

    public ImageViewAdapter(Context context, List<ItemImageUrlList> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        donDownLoadImage = new DownLoadImage(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_image, null);
            holder = new ViewHolder();
            holder.im_url = (ImageView) convertView.findViewById(R.id.im_url);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        donDownLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
        return convertView;
    }

    class ViewHolder {
        private ImageView im_url;
    }

}
