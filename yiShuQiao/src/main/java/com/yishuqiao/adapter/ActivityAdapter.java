package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.yishuqiao.R;
import com.yishuqiao.dto.ActivityDTO;
import com.yishuqiao.utils.DownLoadImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017年7月17日 上午11:05:57 项目名称：YiShuQiao 文件名称：CommentAdapter.java 类说明：作家详情 活动界面
 */
public class ActivityAdapter extends BaseAdapter {

    private List<ActivityDTO.ActivityInfoList> list = null;
    private Context context = null;
    private DownLoadImage downLoadImage = null;

    public ActivityAdapter(Context context, List<ActivityDTO.ActivityInfoList> list) {
        this.context = context;
        this.list = list;
        downLoadImage = new DownLoadImage(context);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_activity_list, null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.content.setText(list.get(position).getTxt_title());

        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.img);
        }

        return convertView;
    }

    private class ViewHolder {

        private TextView content;
        private ImageView img;
    }

}
