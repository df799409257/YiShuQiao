package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.yishuqiao.R;
import com.yishuqiao.dto.MyNewsFragmentDTO;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.YuanJiaoImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 作者：admin 创建时间：2017-7-6 下午2:07:32 项目名称：YiShuQiao 文件名称：MyLookForWorksFragmentAdapter.java 类说明：我发起的 找作品界面
 */
public class MyLookForWorksFragmentAdapter extends BaseAdapter {

    private List<MyNewsFragmentDTO.SubmitFindList> list = null;
    private Context context = null;
    private DownLoadImage downLoadImage = null;

    public MyLookForWorksFragmentAdapter(Context context, List<MyNewsFragmentDTO.SubmitFindList> list) {
        this.list = list;
        this.context = context;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_communication_fragment_item_layout,
                    null);

            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            holder.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
            holder.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
            holder.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_share = (TextView) convertView.findViewById(R.id.tv_share);
            holder.tv_favorite = (TextView) convertView.findViewById(R.id.tv_favorite);
            holder.img = (YuanJiaoImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setTag(list.get(position).getTxt_imglist().get(0).getTxt_url());

        if (!TextUtils.isEmpty(list.get(position).getTxt_imglist().get(0).getTxt_url())) {
            downLoadImage.DisplayImage(list.get(position).getTxt_imglist().get(0).getTxt_url(), holder.img);
        }

        holder.tv_time.setText(list.get(position).getTxt_time());
        holder.tv_state.setText(list.get(position).getTxt_state());
        holder.tv_talknum.setText(list.get(position).getTxt_talk());
        holder.tv_praise.setText(list.get(position).getTxt_praise());
        holder.tv_browsenum.setText(list.get(position).getTxt_show());
        holder.tv_content.setText(list.get(position).getTxt_content());
        holder.tv_share.setText(list.get(position).getTxt_share());
        holder.tv_favorite.setText(list.get(position).getTxt_collect());

        return convertView;
    }

    private class ViewHolder {

        private TextView tv_time;
        private TextView tv_state;
        private TextView tv_talknum;
        private TextView tv_praise;
        private TextView tv_browsenum;
        private TextView tv_content;
        private TextView tv_favorite;
        private TextView tv_share;
        private YuanJiaoImageView img;

    }

}
