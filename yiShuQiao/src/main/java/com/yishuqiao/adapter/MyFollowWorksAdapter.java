package com.yishuqiao.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.MyFollowWorksDTO;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-6 下午3:13:48
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：MyFollowWorksAdapter.java
 * <p>
 * 类说明：我关注的作品界面
 */
public class MyFollowWorksAdapter extends BaseAdapter {

    private List<MyFollowWorksDTO.DataInfor> list = null;
    private Context context = null;

    public MyFollowWorksAdapter(Context context,
                                List<MyFollowWorksDTO.DataInfor> list) {

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_my_follow_works_item_layout, null);

            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.worksName = (TextView) convertView
                    .findViewById(R.id.worksName);

            holder.worksPic = (ImageView) convertView
                    .findViewById(R.id.worksPic);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        if (!TextUtils.isEmpty(list.get(position).getTxt_worksPic())) {
            Picasso.with(context).load(list.get(position).getTxt_worksPic())
                    .fit().error(R.drawable.icon_error).into(holder.worksPic);
        }

        holder.date.setText(list.get(position).getTxt_date());
        holder.worksName.setText(list.get(position).getTxt_worksName());

        return convertView;
    }

    private class ViewHolder {

        private ImageView worksPic;
        private TextView worksName;
        private TextView date;

    }

}
