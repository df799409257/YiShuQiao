package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.activity.discover.WriterPersonalCenterActivity;
import com.yishuqiao.dto.NewsDTO.NewInfor;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.RoundImageView;
import com.yishuqiao.view.YuanJiaoImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentFistAdapter extends BaseAdapter {

    private Handler handler;
    private List<NewInfor> list;
    private Context context;
    private RecyclerViewAdapter adapter;
    private DownLoadImage downLoadImage;

    public FragmentFistAdapter(Context context, List<NewInfor> list, Handler handler) {
        this.context = context;
        this.list = list;
        this.handler = handler;
        this.downLoadImage = new DownLoadImage(context);
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_fragmentfirst, null);
            holder = new ViewHolder();
            holder.writerCenter = (LinearLayout) convertView.findViewById(R.id.writerCenter);
            holder.im_url = (RoundImageView) convertView.findViewById(R.id.im_url);
            holder.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
            holder.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
            holder.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
            holder.recyclerview = (RecyclerView) convertView.findViewById(R.id.id_recyclerview);
            holder.recyclerview.setLayoutManager(new LinearLayoutManager(context));
            holder.card_imageview_one = (YuanJiaoImageView) convertView.findViewById(R.id.card_imageview_one);
            holder.card_imageview_tow = (YuanJiaoImageView) convertView.findViewById(R.id.card_imageview_tow);
            holder.rl_imageview = (LinearLayout) convertView.findViewById(R.id.rl_imageview);
            holder.rl_recyclerview = (RelativeLayout) convertView.findViewById(R.id.rl_recyclerview);

            // 设置布局管理器 设置recyclerview的显示高度
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                    false);
            holder.recyclerview.setLayoutManager(linearLayoutManager);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_guanzhu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        });

        holder.im_url.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WriterPersonalCenterActivity.class);
                intent.putExtra(WriterPersonalCenterActivity.USERID, list.get(position).getTxt_userid());
                intent.putExtra(WriterPersonalCenterActivity.TITLE, list.get(position).getTxt_name());
                context.startActivity(intent);
            }
        });

        if (list.get(position).getTxt_isfollow().equals("0")) {
            holder.tv_guanzhu.setText("关注");
            holder.tv_guanzhu.setBackgroundResource(R.drawable.corners_red_bg);
        } else if (list.get(position).getTxt_isfollow().equals("1")) {
            holder.tv_guanzhu.setText("已关注");
            holder.tv_guanzhu.setBackgroundResource(R.drawable.corners_gray_bg);
        }

        if (!((list.get(position).getTxt_imglist()).isEmpty()) && (list.get(position).getTxt_imglist()).size() == 1) {

            holder.rl_imageview.setVisibility(View.VISIBLE);
            holder.rl_recyclerview.setVisibility(View.GONE);

            holder.card_imageview_one.setVisibility(View.VISIBLE);
            holder.card_imageview_tow.setVisibility(View.GONE);

            if ((list.get(position).getTxt_imglist().size() > 0)
                    && !TextUtils.isEmpty(list.get(position).getTxt_imglist().get(0).getTxt_url())) {
                downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(0).getTxt_url(),
                        holder.card_imageview_one);

                // downLoadImage.DisplayImage(list.get(position).getItemImageUrlList().get(0).getTxt_url(),
                // holder.card_imageview);

            }

        } else if (!((list.get(position).getTxt_imglist()).isEmpty())
                && (list.get(position).getTxt_imglist()).size() == 2) {

            holder.rl_imageview.setVisibility(View.VISIBLE);
            holder.rl_recyclerview.setVisibility(View.GONE);

            holder.card_imageview_one.setVisibility(View.VISIBLE);
            holder.card_imageview_tow.setVisibility(View.VISIBLE);

            if ((list.get(position).getTxt_imglist().size() > 0)
                    && !TextUtils.isEmpty(list.get(position).getTxt_imglist().get(0).getTxt_url())) {
                downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(0).getTxt_url(),
                        holder.card_imageview_one);
                downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(1).getTxt_url(),
                        holder.card_imageview_tow);

            }

        } else if (!((list.get(position).getTxt_imglist()).isEmpty())
                && (list.get(position).getTxt_imglist()).size() > 0) {

            holder.rl_recyclerview.setVisibility(View.VISIBLE);
            holder.rl_imageview.setVisibility(View.GONE);

        } else {
            holder.rl_imageview.setVisibility(View.GONE);
            holder.rl_recyclerview.setVisibility(View.GONE);
        }

        adapter = new RecyclerViewAdapter(context, list.get(position).getTxt_imglist(), downLoadImage);

        // recyclerview点击事件监听
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int index) {
                Message message = Message.obtain();
                message.what = 0;
                message.arg1 = position;
                handler.sendMessage(message);
            }
        });

        holder.recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        holder.tv_name.setText(list.get(position).getTxt_name());
        holder.tv_time.setText(list.get(position).getTxt_time());
        holder.tv_content.setText(list.get(position).getTxt_content());
        holder.tv_browsenum.setText(list.get(position).getTxt_show());
        holder.tv_talknum.setText(list.get(position).getTxt_talk());
        holder.tv_praise.setText(list.get(position).getTxt_praise());
        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
        }

        holder.writerCenter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.arg1 = position;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });

        return convertView;
    }

    static class ViewHolder {

        private LinearLayout writerCenter, rl_imageview;
        private RelativeLayout rl_recyclerview;
        private YuanJiaoImageView card_imageview_one, card_imageview_tow;
        private RecyclerView recyclerview;
        private TextView tv_guanzhu;
        private RoundImageView im_url;
        private TextView tv_name, tv_time, tv_content, tv_browsenum, tv_talknum, tv_praise;
    }

}
