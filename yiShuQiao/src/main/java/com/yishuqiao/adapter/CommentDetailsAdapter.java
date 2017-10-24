package com.yishuqiao.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.CommentDetailActivity;
import com.yishuqiao.activity.CommentWebViewActivity;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.view.RoundImageView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName DiscoverDetailsListViewAdapter
 * @Description TODO(评论详情适配器)
 * @Date 2017年7月25日 上午9:57:45
 */
public class CommentDetailsAdapter extends BaseAdapter {

    private Context context = null;
    private List<CommentListDTO.ReviewInforList> datas = null;

    public CommentDetailsAdapter(Context context, List<CommentListDTO.ReviewInforList> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return this.datas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_comment_details, null);

            holder.praise_layout = (LinearLayout) convertView.findViewById(R.id.praise_layout);

            holder.userIcon = (RoundImageView) convertView.findViewById(R.id.userIcon);

            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.comment_num = (TextView) convertView.findViewById(R.id.comment_num);
            holder.praise = (ImageView) convertView.findViewById(R.id.praise);
            holder.chat_praise = (TextView) convertView.findViewById(R.id.chat_praise);
            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(datas.get(position).getTxt_url())) {
            Picasso.with(context).load(datas.get(position).getTxt_url()).fit().error(R.drawable.default_user_icon)
                    .into(holder.userIcon);
        }

        holder.userIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CommentDetailActivity) context).toWriterPersonalCenter(position);
            }
        });


        holder.userName.setText(datas.get(position).getTxt_username());
        holder.date.setText(datas.get(position).getTxt_time());
        holder.comment_num.setText(datas.get(position).getTxt_recount());
        holder.chat_praise
                .setText((datas.get(position).getTxt_praise()).equals("赞") ? "0" : datas.get(position).getTxt_praise());
        holder.content.setText(datas.get(position).getTxt_content());

        if ((datas.get(position).getTxt_ispraise()).equals("1")) {
            holder.praise.setImageResource(R.drawable.liked);
        } else {
            holder.praise.setImageResource(R.drawable.like_n);
            holder.praise_layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    ((CommentDetailActivity) context).commitCommentPraise(position);

                }
            });
        }

        return convertView;
    }

    private class ViewHolder {

        private RoundImageView userIcon;
        private TextView userName, date, comment_num, chat_praise, content;
        private ImageView praise;
        private LinearLayout praise_layout;

    }

}
