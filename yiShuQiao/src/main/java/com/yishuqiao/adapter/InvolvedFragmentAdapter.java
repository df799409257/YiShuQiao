package com.yishuqiao.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.UserJoinMsgDTO;
import com.yishuqiao.view.RoundImageView;

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
 * 作者：admin 创建时间：2017-7-5 下午6:18:10 项目名称：YiShuQiao 文件名称：InvolvedFragmentAdapter.java 类说明：消息界面 交流
 */
public class InvolvedFragmentAdapter extends BaseAdapter {

    private List<UserJoinMsgDTO.NewsInfor> datas = null;
    private Context context = null;

    public InvolvedFragmentAdapter(Context context, List<UserJoinMsgDTO.NewsInfor> datas) {

        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {

        return datas.size();
    }

    @Override
    public Object getItem(int position) {

        return datas.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_involvedfragment, null);

            holder.picture = (ImageView) convertView.findViewById(R.id.picture);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.usericon = (RoundImageView) convertView.findViewById(R.id.usericon);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_type.setText(datas.get(position).getTxt_toptitle());
        holder.tv_title.setText(datas.get(position).getTxt_title());
        holder.tv_date.setText(datas.get(position).getTxt_time());
        holder.tv_content.setText(datas.get(position).getTxt_content());

        if (!TextUtils.isEmpty(datas.get(position).getTxt_userpic())) {
            Picasso.with(context).load(datas.get(position).getTxt_userpic()).error(R.drawable.default_user_icon).fit()
                    .into(holder.usericon);
        }

        if (!TextUtils.isEmpty(datas.get(position).getTxt_detailpic())) {
            Picasso.with(context).load(datas.get(position).getTxt_detailpic()).error(R.drawable.default_user_icon).fit()
                    .into(holder.picture);
        }

        return convertView;
    }

    private class ViewHolder {

        private RoundImageView usericon;// 头像
        private TextView tv_type;// 消息类型
        private TextView tv_content;// 消息内容
        private TextView tv_title;// 消息标题
        private TextView tv_date;// 消息时间
        private ImageView picture;// 消息图片

    }

}
