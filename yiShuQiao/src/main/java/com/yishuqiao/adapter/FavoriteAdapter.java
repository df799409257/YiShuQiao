package com.yishuqiao.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.FavoriteDTO;
import com.yishuqiao.view.YuanJiaoImageView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017-7-6 下午2:46:15 项目名称：YiShuQiao 文件名称：FavoriteAdapter.java 类说明：我的收藏
 */
public class FavoriteAdapter extends BaseAdapter {

    private Handler handler;
    private List<FavoriteDTO.CollectInfoList> list = null;
    private Context context = null;

    public FavoriteAdapter(Context context, List<FavoriteDTO.CollectInfoList> list, Handler handler) {

        this.context = context;
        this.list = list;
        this.handler = handler;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_favorite, null);

            holder.cancle = (RelativeLayout) convertView.findViewById(R.id.cancle);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.worksTittle = (TextView) convertView.findViewById(R.id.worksTittle);
            holder.worksPic = (YuanJiaoImageView) convertView.findViewById(R.id.worksPic);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(list.get(position).getTxt_img())) {
            Picasso.with(context).load(list.get(position).getTxt_img()).fit().error(R.drawable.icon_error)
                    .into(holder.worksPic);
        }

        holder.date.setText(list.get(position).getTxt_creattime());
        holder.worksTittle.setText(list.get(position).getTxt_title());

        holder.cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Message msg = Message.obtain();
                msg.what = 0;
                msg.arg1 = position;
                handler.sendMessage(msg);

            }
        });

        return convertView;
    }

    private class ViewHolder {

        private YuanJiaoImageView worksPic;
        private TextView worksTittle;
        private TextView date;
        private RelativeLayout cancle;
    }

}
