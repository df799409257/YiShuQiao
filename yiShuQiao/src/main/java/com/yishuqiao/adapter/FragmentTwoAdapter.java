package com.yishuqiao.adapter;

import java.io.Serializable;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.ImageDetailsActivity;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.RoundImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentTwoAdapter extends BaseAdapter {

    private List<FindItem> list;
    private Context context;
    private FragmentTwoGridViewAdapter adapter;
    private Handler handler;
    private DownLoadImage downLoadImage;

    public FragmentTwoAdapter(Context context, List<FindItem> list, Handler handler) {
        this.context = context;
        this.list = list;
        this.handler = handler;
        downLoadImage = new DownLoadImage(context);
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
    public View getView(final int position, View convertView, ViewGroup arg2) {
        convertView = null;
        if (list.get(position).getTxt_type().equals("1")) {// 1是图片 2是视频
            return init_CreatOne(position, convertView);
        } else {
            return init_CreatTwo(position, convertView);
        }
    }

    class ViewHolder {

        private ImageView im_ima;
        private RoundImageView im_url;
        private GridView gv_gridview;
        private TextView tv_name, tv_time, tv_content, tv_talknum, tv_praise, tv_guanzhu, tv_browsenum;
        private RelativeLayout re_bofang;
    }

    public View init_CreatOne(final int position, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_fragmenttwo_one, null);
            holder = new ViewHolder();
            holder.im_url = (RoundImageView) convertView.findViewById(R.id.im_url);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
            holder.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
            holder.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
            holder.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
            holder.gv_gridview = (GridView) convertView.findViewById(R.id.gv_gridview);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        adapter = new FragmentTwoGridViewAdapter(context, list.get(position).getTxt_imglist(), downLoadImage);
        holder.gv_gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        holder.gv_gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(context, ImageDetailsActivity.class);
                intent.putExtra("FindItem", (Serializable) list.get(position).getItemImageUrlListOriginal());
                context.startActivity(intent);
            }
        });

        holder.tv_name.setText(list.get(position).getTxt_name());
        holder.tv_time.setText(list.get(position).getTxt_time());
        holder.tv_content.setText(list.get(position).getTxt_content());
        holder.tv_talknum.setText(list.get(position).getTxt_talk());
        holder.tv_browsenum.setText(list.get(position).getTxt_show());
        holder.tv_praise.setText(list.get(position).getTxt_praise());

        if (list.get(position).getTxt_isfollow().equals("0")) {
            holder.tv_guanzhu.setText("关注");
            holder.tv_guanzhu.setBackgroundColor(Color.rgb(251, 71, 72));
        } else if (list.get(position).getTxt_isfollow().equals("1")) {
            holder.tv_guanzhu.setText("已关注");
            holder.tv_guanzhu.setBackgroundColor(Color.rgb(172, 172, 172));
        }

        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            Picasso.with(context).load(list.get(position).getTxt_url()).error(R.drawable.default_user_icon).fit()
                    .into(holder.im_url);
        }

        holder.tv_guanzhu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Message msg = Message.obtain();
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        });
        return convertView;
    }

    public View init_CreatTwo(final int position, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_fragmenttwo_two, null);
            holder = new ViewHolder();
            holder.im_url = (RoundImageView) convertView.findViewById(R.id.im_url);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
            holder.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
            holder.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
            holder.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
            holder.re_bofang = (RelativeLayout) convertView.findViewById(R.id.re_bofang);
            holder.im_ima = (ImageView) convertView.findViewById(R.id.im_ima);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).getTxt_name());
        holder.tv_time.setText(list.get(position).getTxt_time());
        holder.tv_content.setText(list.get(position).getTxt_content());
        holder.tv_talknum.setText(list.get(position).getTxt_talk());
        holder.tv_browsenum.setText(list.get(position).getTxt_show());
        holder.tv_praise.setText(list.get(position).getTxt_praise());

        // holder.re_bofang.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // // TODO Auto-generated method stub 点击视频跳转到视频详情
        // Intent intent = new Intent(context, VideoPlayActivity.class);
        // intent.putExtra(VideoPlayActivity.VIDEO_PLAY_ID, list.get(position).getTxt_id());
        // intent.putExtra(VideoPlayActivity.VIDEO_PLAY_TITLE, list.get(position).getTxt_content());
        // context.startActivity(intent);
        // }
        // });

        if (list.get(position).getTxt_imglist().size() != 0) {
            downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(0).getTxt_url(), holder.im_ima);
        }

        if (list.get(position).getTxt_isfollow().equals("0")) {
            holder.tv_guanzhu.setText("关注");
            holder.tv_guanzhu.setBackgroundColor(Color.rgb(251, 71, 72));
        } else if (list.get(position).getTxt_isfollow().equals("1")) {
            holder.tv_guanzhu.setText("已关注");
            holder.tv_guanzhu.setBackgroundColor(Color.rgb(172, 172, 172));
        }

        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            Picasso.with(context).load(list.get(position).getTxt_url()).error(R.drawable.default_user_icon).fit()
                    .into(holder.im_url);
        }
        holder.tv_guanzhu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Message msg = Message.obtain();
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        });
        return convertView;
    }

}
