package com.yishuqiao.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.YiYuanDTO.YiYuanInfor;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.YuanJiaoImageView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FragmentFourAdapter extends BaseAdapter {

    private List<YiYuanInfor> list;
    private Context context;
    private DownLoadImage downLoadImage;

    public FragmentFourAdapter(Context context, List<YiYuanInfor> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_fragmentfour, null);
            holder = new ViewHolder();
            holder.im_url = (YuanJiaoImageView) convertView.findViewById(R.id.im_url);

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.im_url.setTag(list.get(position).getTxt_url());
        holder.tv_name.setText(list.get(position).getTxt_name());
        if (!TextUtils.isEmpty(list.get(position).getTxt_url())) {
            if (list.get(position).getTxt_url().equals(holder.im_url.getTag())) {
                downLoadImage.DisplayImage(list.get(position).getTxt_url(), holder.im_url);
            }
        }
        return convertView;
    }

    class ViewHolder {

        private YuanJiaoImageView im_url;
        private TextView tv_name;
    }

}
