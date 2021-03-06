package com.yishuqiao.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.mine.MyFansActivity;
import com.yishuqiao.dto.MyFansDTO;
import com.yishuqiao.view.RoundImageView;

/**
 * 作者：admin 创建时间：2017-7-6 上午10:41:36 项目名称：YiShuQiao 文件名称：MyFansAdapter.java 类说明：我的粉丝列表
 */
public class MyFansAdapter extends BaseAdapter {

    private List<MyFansDTO.FollowInforList> list = null;
    private Context context = null;

    public MyFansAdapter(Context context, List<MyFansDTO.FollowInforList> list) {

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolde holder = null;
        if (convertView == null) {

            holder = new ViewHolde();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_myfans, null);

            holder.txt_state = (TextView) convertView.findViewById(R.id.tv_state);
            holder.txt_name = (TextView) convertView.findViewById(R.id.tv_userName);
            holder.txt_date = (TextView) convertView.findViewById(R.id.tv_time);
            holder.txt_userpic = (RoundImageView) convertView.findViewById(R.id.iv_userHead);

            holder.rl_bt = (RelativeLayout) convertView.findViewById(R.id.rl_bt);
            holder.iv_bt = (ImageView) convertView.findViewById(R.id.iv_bt);
            holder.tv_bt = (TextView) convertView.findViewById(R.id.tv_bt);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolde) convertView.getTag();
        }

        holder.txt_name.setText(list.get(position).getTxt_nickname());
        holder.txt_date.setText(TextUtils.isEmpty(list.get(position).getTxt_followtime()) ? ""
                : "关注时间：" + list.get(position).getTxt_followtime());

        if (!TextUtils.isEmpty(list.get(position).getTxt_imgurl())) {
            Picasso.with(context).load(list.get(position).getTxt_imgurl()).fit().error(R.drawable.default_user_icon)
                    .into(holder.txt_userpic);
        }

        holder.rl_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MyFansActivity) context).submitAttention(position);
            }
        });

        // 根据关注状态显示
        if (list.get(position).getTxt_followme().equals("1")) {
            holder.iv_bt.setImageResource(R.drawable.ald_marl);
            holder.tv_bt.setText("已关注");
            holder.tv_bt.setTextColor(Color.rgb(153, 153, 153));
            holder.rl_bt.setBackgroundResource(R.drawable.ald_attention_mark);
            holder.txt_state.setText("互相关注");
        } else if (list.get(position).getTxt_followme().equals("0")) {
            holder.iv_bt.setImageResource(R.drawable.add_mark);
            holder.tv_bt.setText("关注");
            holder.tv_bt.setTextColor(Color.rgb(251, 71, 72));
            holder.rl_bt.setBackgroundResource(R.drawable.attention_mark);
            holder.txt_state.setText("");
        }

        return convertView;
    }

    private class ViewHolde {

        private RoundImageView txt_userpic;// 头像地址
        private TextView txt_name;// 用户名
        private TextView txt_date;// 日期
        private TextView txt_state;// 关注状态

        // 根据关注状态显示按钮状态
        private RelativeLayout rl_bt;// 关注按钮
        private ImageView iv_bt;
        private TextView tv_bt;

    }

}
