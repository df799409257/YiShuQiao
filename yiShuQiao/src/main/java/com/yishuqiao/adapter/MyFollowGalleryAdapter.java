package com.yishuqiao.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.MyFollowGalleryDTO;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 下午3:35:45
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyFollowGalleryAdapter.java
 * <p>
 * 类说明：我关注的画廊界面
 */
public class MyFollowGalleryAdapter extends BaseAdapter {

    private List<MyFollowGalleryDTO.DataInfor> list = null;
    private Context context = null;

    public MyFollowGalleryAdapter(Context context,
                                  List<MyFollowGalleryDTO.DataInfor> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_my_follow_gallery_item_layout, null);

            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.galleryName = (TextView) convertView
                    .findViewById(R.id.galleryName);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);

            holder.galleryPic = (ImageView) convertView
                    .findViewById(R.id.galleryPic);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        if (!TextUtils.isEmpty(list.get(position).getTxt_galleryPic())) {
            Picasso.with(context).load(list.get(position).getTxt_galleryPic())
                    .error(R.drawable.icon_error).fit().into(holder.galleryPic);
        }

        holder.date.setText(list.get(position).getTxt_date());
        holder.galleryName.setText(list.get(position).getTxt_galleryName());

        if ((list.get(position).getTxt_num()).equals("0")) {
            holder.tv_num.setVisibility(View.GONE);
        } else {
            holder.tv_num.setText(list.get(position).getTxt_num());
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView galleryPic;
        private TextView galleryName;
        private TextView date;
        private TextView tv_num;
    }

}
