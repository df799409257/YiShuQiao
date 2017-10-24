package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.yishuqiao.R;
import com.yishuqiao.dto.SearchDTO;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.YuanJiaoImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName SearchAdapter
 * @Description TODO(搜索界面)
 * @Date 2017年8月3日 下午3:23:49
 */
public class SearchAdapter extends BaseAdapter {

    private Context context = null;
    private List<SearchDTO.SearchList> list = null;
    private DownLoadImage downLoadImage = null;

    public SearchAdapter(Context context, List<SearchDTO.SearchList> list) {

        this.list = list;
        this.context = context;
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

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_search, null);
            holder.searchTittle = (TextView) convertView.findViewById(R.id.searchTittle);
            holder.picture = (YuanJiaoImageView) convertView.findViewById(R.id.picture);
            holder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.searchTittle.setText(list.get(position).getTxt_name());
        holder.date.setText(list.get(position).getTxt_time());
        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.picture);
        }

        return convertView;
    }

    private class ViewHolder {

        private TextView searchTittle;
        private TextView date;
        private YuanJiaoImageView picture;
    }

}
