package com.yishuqiao.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.yishuqiao.R;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.utils.DownLoadImage;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 作者：admin 创建时间：2017年7月19日 下午3:44:59 项目名称：YiShuQiao 文件名称：DiscoverDetailsGradeViewAdapter.java 类说明：首页发现详情界面
 */
public class DiscoverDetailsGradeViewAdapter extends BaseAdapter {

    private Context context = null;
    private List<FindDTO.FindItemImageUrl> datas = null;
    private DownLoadImage downLoadImage = null;

    public DiscoverDetailsGradeViewAdapter(Context context, List<FindDTO.FindItemImageUrl> datas) {
        this.context = context;
        this.datas = datas;
        downLoadImage = new DownLoadImage(context);
    }

    @Override
    public int getCount() {
        return this.datas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_gridview, null);
            holder = new ViewHolder();
            holder.im_url = (ImageView) convertView.findViewById(R.id.im_url);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(datas.get(position).getTxt_url())) {
            downLoadImage.DisplayImage(datas.get(position).getTxt_url(), holder.im_url);
        }

        return convertView;
    }

    class ViewHolder {

        private ImageView im_url;
    }
}
