
package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yishuqiao.R;
import com.yishuqiao.dto.WorkDTO;
import com.yishuqiao.utils.BlurBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017年7月14日 下午7:19:47 项目名称：YiShuQiao 文件名称：WorkAdapter.java 类说明：作家作品列表适配器
 */
public class WorkAdapter extends BaseAdapter {

    private Context context = null;
    private List<WorkDTO.WorksInforList> list = null;
    private RequestQueue mQueue;

    public WorkAdapter(Context context, List<WorkDTO.WorksInforList> list, RequestQueue mQueue) {
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
            arg1 = LayoutInflater.from(context).inflate(R.layout.adapter_work, null);
            holder = new ViewHolder();
            holder.content = (TextView) arg1.findViewById(R.id.content);
            holder.img = (ImageView) arg1.findViewById(R.id.img);
            holder.im_url = (ImageView) arg1.findViewById(R.id.im_url);

            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }

        if (list.size() > 0 && !TextUtils.isEmpty(list.get(arg0).getTxt_url())) {
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


        holder.content.setText(list.get(arg0).getTxt_name());

        return arg1;
    }

    class ViewHolder {

        private ImageView img, im_url;
        private TextView content;
    }

}
