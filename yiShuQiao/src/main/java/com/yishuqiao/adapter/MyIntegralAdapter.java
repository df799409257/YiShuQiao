package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.dto.MyIntegralDTO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 上午10:12:16
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyIntegralAdapter.java
 * <p>
 * 类说明：我的积分界面
 */
public class MyIntegralAdapter extends BaseAdapter {

    private List<MyIntegralDTO.DataInfor> list = null;
    private Context context = null;

    public MyIntegralAdapter(Context context, List<MyIntegralDTO.DataInfor> list) {
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

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_integral_listview, null);

            holder.count = (TextView) convertView.findViewById(R.id.count);
            holder.date = (TextView) convertView.findViewById(R.id.time);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.count
                .setText(TextUtils.isEmpty(list.get(position).getTxt_count()) ? ""
                        : "+" + list.get(position).getTxt_count());
        holder.title.setText(list.get(position).getTxt_title());
        holder.date.setText(list.get(position).getTxt_date());

        return convertView;
    }

    private class ViewHolder {
        private TextView title;
        private TextView date;
        private TextView count;

    }

}
