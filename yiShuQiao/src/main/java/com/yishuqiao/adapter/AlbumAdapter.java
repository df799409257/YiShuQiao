
package com.yishuqiao.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yishuqiao.R;
import com.yishuqiao.activity.ViewPagerActivity;
import com.yishuqiao.dto.AlbumDTO;
import com.yishuqiao.dto.ViewPagerDTO;
import com.yishuqiao.utils.BlurBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName AlbumAdapter
 * @Description TODO(作家详情 相册列表)
 * @Date 2017年7月24日 下午1:59:34
 */
public class AlbumAdapter extends BaseAdapter {

    private Context context = null;
    private List<AlbumDTO.PictureInfoList> list = null;
    private RequestQueue mQueue;

    private ArrayList<ViewPagerDTO> imgs = null;

    public AlbumAdapter(Context context, List<AlbumDTO.PictureInfoList> list, RequestQueue mQueue) {
        this.context = context;
        this.list = list;
        this.mQueue = mQueue;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int arg0) {

        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        if (arg1 == null) {
            arg1 = LayoutInflater.from(context).inflate(R.layout.adapter_album, null);
            holder = new ViewHolder();
            holder.content = (TextView) arg1.findViewById(R.id.content);
            holder.date = (TextView) arg1.findViewById(R.id.date);
            holder.img = (ImageView) arg1.findViewById(R.id.img);
            holder.im_url = (ImageView) arg1.findViewById(R.id.im_url);

            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }

        if (list.size() > 0 && !TextUtils.isEmpty(list.get(arg0).getTxt_smallurl())) {
            ImageLoader.getInstance().displayImage(list.get(arg0).getTxt_url(), holder.im_url,
                    new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            holder.img.setImageResource(R.drawable.icon_error);
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1, Bitmap response) {
                            holder.img.setImageBitmap(BlurBitmap.blur(context,response));
                            holder.im_url.setImageBitmap(response);
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                        }
                    });
        } else {
            holder.img.setImageResource(R.drawable.icon_error);
        }



        holder.content.setText(list.get(arg0).getTxt_title());
        holder.date.setText(list.get(arg0).getTxt_time());

        holder.im_url.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imgs = new ArrayList<ViewPagerDTO>();
                ViewPagerDTO pagerDTO = new ViewPagerDTO();
                pagerDTO.setPic_url(list.get(arg0).getTxt_url());
                pagerDTO.setTxt_content(list.get(arg0).getTxt_title());
                imgs.add(pagerDTO);
                Intent intent = new Intent(context, ViewPagerActivity.class);
                intent.putExtra(ViewPagerActivity.IMGS, (Serializable) imgs);
                intent.putExtra(ViewPagerActivity.INDEX, 0);
                context.startActivity(intent);
            }
        });

        return arg1;
    }

    class ViewHolder {

        private ImageView img, im_url;
        private TextView content, date;
    }

}
