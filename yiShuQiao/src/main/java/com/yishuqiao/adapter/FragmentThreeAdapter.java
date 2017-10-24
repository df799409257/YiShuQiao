package com.yishuqiao.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.yishuqiao.R;
import com.yishuqiao.dto.VideoDTO.ViewInfor;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.utils.Utils;

/**
 * Created by Administrator on 2016-11-29.
 */
public class FragmentThreeAdapter extends BaseAdapter {

    public List<ViewInfor> list;
    public Context context;
    public LayoutInflater inflater;
    public int width;
    public RequestQueue mQueue;
    private DownLoadImage downLoadImage;

    public FragmentThreeAdapter(List<ViewInfor> list, Context context, int width) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.width = width;
        mQueue = Volley.newRequestQueue(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_video, parent, false);
            holder = new ViewHolder();

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_bofang = (TextView) convertView.findViewById(R.id.tv_bofang);

            holder.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
            holder.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            holder.re_back = (RelativeLayout) convertView.findViewById(R.id.re_back);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, 9 * width / 16);
            holder.re_back.setLayoutParams(param);

            holder.im_back = (ImageView) convertView.findViewById(R.id.im_back);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(list.get(position).getTxt_content());
        holder.tv_bofang.setText(list.get(position).getTxt_show() + "次播放");
        holder.tv_praise.setText(list.get(position).getTxt_praise());
        holder.tv_talknum.setText(list.get(position).getTxt_talk());

        holder.tv_time.setText((TextUtils.isEmpty(list.get(position).getTxt_duration())) ? "0"
                : Utils.secToTime(Integer.parseInt(list.get(position).getTxt_duration())));

        if (null != list.get(position).getTxt_imglist() || list.get(position).getTxt_imglist().size() != 0) {
            holder.im_back.setTag(list.get(position).getTxt_imglist().get(0).getTxt_url());
        }

        if ((null != holder.im_back.getTag())
                && holder.im_back.getTag().equals(list.get(position).getTxt_imglist().get(0).getTxt_url())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(0).getTxt_url(), holder.im_back);

        }
        return convertView;
    }

    static class ViewHolder {

        private TextView tv_name, tv_bofang, tv_praise, tv_talknum, tv_time;
        private ImageView im_back;
        private RelativeLayout re_back;
    }

}
