package com.yishuqiao.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.activity.ImageDetailsActivity;
import com.yishuqiao.dto.FindDTO.FindItem;
import com.yishuqiao.utils.DownLoadImage;
import com.yishuqiao.view.RoundImageView;
import com.yishuqiao.view.YuanJiaoImageView;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName FragmentFirstDifferentItemAdapter
 * @Description TODO(FragmentFirst加载不同的item布局)
 * @Date 2017年7月31日 上午10:14:45
 */
public class FragmentFirstDifferentItemAdapter extends BaseAdapter {

    private List<FindItem> list;
    private Context context;
    private FragmentTwoGridViewAdapter adapter;
    private Handler handler;
    private DownLoadImage downLoadImage;

    final int VIEW_TYPE = 3;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;

    public FragmentFirstDifferentItemAdapter(Context context, List<FindItem> list, Handler handler) {
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

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        String p = list.get(position).getTxt_type();
        if (p.equals("1"))
            return TYPE_1;
        else
            return TYPE_2;

    }

    // 需要注意的地方：adapter中的getViewTypeCount()方法一定要重载，返回值应该是不同布局的种类数
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        final FindItem findItem = list.get(position);
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = View.inflate(context, R.layout.adapter_fragmenttwo_one, null);
                    holder1 = new ViewHolder1();
                    holder1.writerCenter = (LinearLayout) convertView.findViewById(R.id.writerCenter);
                    holder1.rl_pic = (RelativeLayout) convertView.findViewById(R.id.rl_pic);
                    holder1.rl_gv = (RelativeLayout) convertView.findViewById(R.id.rl_gv);
                    holder1.im_pic1 = (YuanJiaoImageView) convertView.findViewById(R.id.im_pic1);
                    holder1.im_pic2 = (YuanJiaoImageView) convertView.findViewById(R.id.im_pic2);
                    holder1.im_url = (RoundImageView) convertView.findViewById(R.id.im_url);
                    holder1.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder1.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder1.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    holder1.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
                    holder1.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
                    holder1.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
                    holder1.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
                    holder1.gv_gridview = (GridView) convertView.findViewById(R.id.gv_gridview);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = View.inflate(context, R.layout.adapter_fragmenttwo_two, null);
                    holder2 = new ViewHolder2();
                    holder2.writerCenter = (LinearLayout) convertView.findViewById(R.id.writerCenter);
                    holder2.im_url = (RoundImageView) convertView.findViewById(R.id.im_url);
                    holder2.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder2.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder2.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    holder2.tv_talknum = (TextView) convertView.findViewById(R.id.tv_talknum);
                    holder2.tv_browsenum = (TextView) convertView.findViewById(R.id.tv_browsenum);
                    holder2.tv_praise = (TextView) convertView.findViewById(R.id.tv_praise);
                    holder2.tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
                    holder2.im_ima = (YuanJiaoImageView) convertView.findViewById(R.id.im_ima);
                    convertView.setTag(holder2);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;

            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1:

                if (!((findItem.getTxt_imglist()).isEmpty()) && findItem.getTxt_imglist().size() == 1) {

                    holder1.rl_pic.setVisibility(View.VISIBLE);
                    holder1.rl_gv.setVisibility(View.GONE);

                    holder1.im_pic1.setVisibility(View.VISIBLE);
                    holder1.im_pic2.setVisibility(View.GONE);

                    holder1.im_pic1.setTag(findItem.getTxt_imglist().get(0).getTxt_url());

                    if (null != holder1.im_pic1.getTag()
                            && findItem.getTxt_imglist().get(0).getTxt_url().equals(holder1.im_pic1.getTag())) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(0).getTxt_url(), holder1.im_pic1);
                    }
                    holder1.im_pic1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, ImageDetailsActivity.class);
                            intent.putExtra(ImageDetailsActivity.FindItem,
                                    (Serializable) findItem.getItemImageUrlListOriginal());
                            context.startActivity(intent);
                        }
                    });
                } else if (!(findItem.getTxt_imglist()).isEmpty() && (findItem.getTxt_imglist()).size() == 2) {

                    holder1.rl_pic.setVisibility(View.VISIBLE);
                    holder1.rl_gv.setVisibility(View.GONE);

                    holder1.im_pic1.setVisibility(View.VISIBLE);
                    holder1.im_pic2.setVisibility(View.VISIBLE);

                    holder1.im_pic1.setTag(findItem.getTxt_imglist().get(0).getTxt_url());
                    holder1.im_pic2.setTag(findItem.getTxt_imglist().get(1).getTxt_url());

                    if (findItem.getTxt_imglist().get(0).getTxt_url().equals(holder1.im_pic1.getTag())) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(0).getTxt_url(), holder1.im_pic1);
                    }
                    if (findItem.getTxt_imglist().get(1).getTxt_url().equals(holder1.im_pic2.getTag())) {
                        downLoadImage.DisplayImage(findItem.getTxt_imglist().get(1).getTxt_url(), holder1.im_pic2);
                    }
                    holder1.im_pic1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, ImageDetailsActivity.class);
                            intent.putExtra(ImageDetailsActivity.FindItem,
                                    (Serializable) findItem.getItemImageUrlListOriginal());
                            intent.putExtra(ImageDetailsActivity.PAGER_INDEX, 0);
                            context.startActivity(intent);
                        }
                    });
                    holder1.im_pic2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, ImageDetailsActivity.class);
                            intent.putExtra(ImageDetailsActivity.FindItem,
                                    (Serializable) findItem.getItemImageUrlListOriginal());
                            intent.putExtra(ImageDetailsActivity.PAGER_INDEX, 1);
                            context.startActivity(intent);
                        }
                    });
                } else {

                    holder1.rl_pic.setVisibility(View.GONE);
                    holder1.rl_gv.setVisibility(View.VISIBLE);

                    adapter = new FragmentTwoGridViewAdapter(context, findItem.getTxt_imglist(), downLoadImage);
                    holder1.gv_gridview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    holder1.gv_gridview.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            intent.setClass(context, ImageDetailsActivity.class);
                            intent.putExtra(ImageDetailsActivity.FindItem,
                                    (Serializable) findItem.getItemImageUrlListOriginal());
                            intent.putExtra(ImageDetailsActivity.PAGER_INDEX, arg2);
                            context.startActivity(intent);
                        }
                    });
                }

                holder1.tv_name.setText(findItem.getTxt_name());
                holder1.tv_time.setText(findItem.getTxt_time());
                holder1.tv_content.setText(findItem.getTxt_content());
                holder1.tv_talknum.setText(findItem.getTxt_talk());
                holder1.tv_browsenum.setText(findItem.getTxt_show());
                holder1.tv_praise.setText(findItem.getTxt_praise());

                if (findItem.getTxt_isfollow().equals("0")) {
                    holder1.tv_guanzhu.setText("关注");
                    holder1.tv_guanzhu.setBackgroundResource(R.drawable.corners_red_bg);
                } else if (findItem.getTxt_isfollow().equals("1")) {
                    holder1.tv_guanzhu.setText("已关注");
                    holder1.tv_guanzhu.setBackgroundResource(R.drawable.corners_gray_bg);
                }

                if (!TextUtils.isEmpty(findItem.getTxt_url())) {
                    downLoadImage.DisplayImage(findItem.getTxt_url(), holder1.im_url);
                }

                holder1.tv_guanzhu.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                });

                holder1.writerCenter.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                });

                break;
            case TYPE_2:

                holder2.tv_name.setText(findItem.getTxt_name());
                holder2.tv_time.setText(findItem.getTxt_time());
                holder2.tv_content.setText(findItem.getTxt_content());
                holder2.tv_talknum.setText(findItem.getTxt_talk());
                holder2.tv_browsenum.setText(findItem.getTxt_show());
                holder2.tv_praise.setText(findItem.getTxt_praise());

                if ((findItem.getTxt_imglist()).size() > 0) {
                    holder2.im_ima.setTag(findItem.getTxt_imglist().get(0).getTxt_url());
                }

                if ((findItem.getTxt_imglist()).size() > 0 && null != holder2.im_ima.getTag()
                        && findItem.getTxt_imglist().get(0).getTxt_url().equals(holder2.im_ima.getTag())) {
                    downLoadImage.DisplayImage(findItem.getTxt_imglist().get(0).getTxt_url(), holder2.im_ima);
                }

                if (findItem.getTxt_isfollow().equals("0")) {
                    holder2.tv_guanzhu.setText("关注");
                    holder2.tv_guanzhu.setBackgroundResource(R.drawable.corners_red_bg);
                } else if (findItem.getTxt_isfollow().equals("1")) {
                    holder2.tv_guanzhu.setText("已关注");
                    holder2.tv_guanzhu.setBackgroundResource(R.drawable.corners_gray_bg);
                }

                if (!TextUtils.isEmpty(findItem.getTxt_url())) {
                    downLoadImage.DisplayImage(findItem.getTxt_url(), holder2.im_url);
                }
                holder2.tv_guanzhu.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                });

                holder2.writerCenter.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Message msg = Message.obtain();
                        msg.arg1 = position;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                });

                break;
        }

        return convertView;

    }

    static class ViewHolder1 {

        private LinearLayout writerCenter;
        private RelativeLayout rl_pic, rl_gv;
        private RoundImageView im_url;
        private YuanJiaoImageView im_pic1, im_pic2;
        private GridView gv_gridview;
        private TextView tv_name, tv_time, tv_content, tv_talknum, tv_praise, tv_guanzhu, tv_browsenum;

    }

    static class ViewHolder2 {

        private LinearLayout writerCenter;
        private YuanJiaoImageView im_ima;
        private RoundImageView im_url;
        private TextView tv_name, tv_time, tv_content, tv_talknum, tv_praise, tv_guanzhu, tv_browsenum;
    }

}
