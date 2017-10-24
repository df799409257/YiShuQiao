package com.yishuqiao.adapter;

import java.util.List;

import com.yishuqiao.R;
import com.yishuqiao.dto.NewsDTO.NewInfor.ItemImageUrlList;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.YuanJiaoImageView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;// 点击事件
    private OnItemLongClickListener mOnItemLongClickListener;// 长按事件

    private List<ItemImageUrlList> list;
    private Context context;
    private DownLoadImage downLoadImage;

    public RecyclerViewAdapter(Context context, List<ItemImageUrlList> list, DownLoadImage downLoadImage) {
        this.list = list;
        this.context = context;
        this.downLoadImage = downLoadImage;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View arg0) {
            super(arg0);
        }

        private YuanJiaoImageView im_url;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.adapter_image, viewGroup, false);
        ViewHolder holder = new ViewHolder(convertView);

        holder.im_url = (YuanJiaoImageView) convertView.findViewById(R.id.im_url);

        return holder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.im_url.setTag(list.get(position).getTxt_url());

        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            if (list.get(position).getTxt_url().equals(holder.im_url.getTag())) {
                downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
            }
            // ImageLoader.getInstance().displayImage(list.get(position).getTxt_url(), holder.im_url);
            // downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
        }

        // 判断是否设置了监听器
        if (mOnItemClickListener != null) {
            // 为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = holder.getPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                    // 返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

}
