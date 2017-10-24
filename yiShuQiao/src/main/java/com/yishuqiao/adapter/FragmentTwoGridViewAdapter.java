package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.dto.FindDTO.FindItemImageUrl;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.YuanJiaoImageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FragmentTwoGridViewAdapter extends BaseAdapter {

    private List<FindItemImageUrl> list;
    private Context context;
    private DownLoadImage downLoadImage;

    public FragmentTwoGridViewAdapter(Context context, List<FindItemImageUrl> list, DownLoadImage downLoadImage) {
        this.context = context;
        this.list = list;
        this.downLoadImage = downLoadImage;
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
            convertView = View.inflate(context, R.layout.adapter_gridview, null);
            holder = new ViewHolder();
            holder.im_url = (YuanJiaoImageView) convertView.findViewById(R.id.im_url);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.im_url.setTag(list.get(position).getTxt_url());
        if (holder.im_url.getTag() != null && list.get(position).getTxt_url().equals(holder.im_url.getTag())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
        }
        return convertView;
    }

    static class ViewHolder {

        private YuanJiaoImageView im_url;
    }

}
