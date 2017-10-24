package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.yishuqiao.R;
import com.yishuqiao.activity.video.VideoPlayActivity;
import com.yishuqiao.dto.CommentListDTO;
import com.yishuqiao.utils.ViewHolder;
import com.yishuqiao.view.RoundImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-12-20.
 */
public class VideoPlayAdapter extends BaseAdapter {

    private RequestQueue mQueue;
    private List<CommentListDTO.ReviewInforList> list;
    private Context context;

    public VideoPlayAdapter(Context context, List<CommentListDTO.ReviewInforList> list) {
        this.context = context;
        this.list = list;
        mQueue = Volley.newRequestQueue(context);
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_videoplay, parent, false);
        }

        TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
        TextView tv_zan = ViewHolder.get(convertView, R.id.tv_zan);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
        TextView tv_pinglun = ViewHolder.get(convertView, R.id.tv_pinglun);
        LinearLayout zan = ViewHolder.get(convertView, R.id.zan);

        final ImageView im_zan = ViewHolder.get(convertView, R.id.im_zan);
        if ((list.get(position).getTxt_ispraise()).equals("0")) {
            im_zan.setBackgroundResource(R.drawable.like_n);

            zan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((VideoPlayActivity) context).commitPraise(position);
                }
            });

        } else {
            im_zan.setBackgroundResource(R.drawable.liked);
        }
        final RoundImageView im_url = ViewHolder.get(convertView, R.id.im_url);
        tv_zan.setText((list.get(position).getTxt_praise()).equals("赞") ? "0" : list.get(position).getTxt_praise());
        tv_name.setText(list.get(position).getTxt_username());
        tv_time.setText(list.get(position).getTxt_time());
        tv_pinglun.setText(list.get(position).getTxt_recount());
        tv_content.setText(list.get(position).getTxt_content());

        ImageRequest imageRequest = new ImageRequest(list.get(position).getTxt_url(), new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                im_url.setImageBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                im_url.setImageResource(R.drawable.default_user_icon);
            }
        });
        mQueue.add(imageRequest);

        im_url.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((VideoPlayActivity) context).toWriterCenter(position);
            }
        });


        return convertView;
    }
}
