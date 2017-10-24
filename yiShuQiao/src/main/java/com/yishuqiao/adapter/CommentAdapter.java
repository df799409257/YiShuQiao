package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.dto.CommentDTO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017年7月17日 上午11:05:57 项目名称：YiShuQiao 文件名称：CommentAdapter.java 类说明：作家详情 讨论界面
 */
public class CommentAdapter extends BaseAdapter {

    private List<CommentDTO.CommmentInfoList> list = null;
    private Context context = null;

    public CommentAdapter(Context context, List<CommentDTO.CommmentInfoList> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_commentfragment_list, null);
            holder = new ViewHolder();
            holder.comment_num = (TextView) convertView.findViewById(R.id.comment_num);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.read_num = (TextView) convertView.findViewById(R.id.read_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.comment_num.setText(TextUtils.isEmpty(list.get(position).getTxt_discuss()) ? ""
                : list.get(position).getTxt_discuss() + "讨论");
        holder.read_num.setText(
                TextUtils.isEmpty(list.get(position).getTxt_reads()) ? "" : list.get(position).getTxt_reads() + "阅读");
        holder.content.setText(list.get(position).getTxt_content());
        holder.title.setText(list.get(position).getTxt_title());

        return convertView;
    }

    private class ViewHolder {

        private TextView title;
        private TextView content;
        private TextView comment_num;
        private TextView read_num;
    }

}
