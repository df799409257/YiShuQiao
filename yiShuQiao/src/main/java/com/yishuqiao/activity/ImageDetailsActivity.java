package com.yishuqiao.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.internal.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.adapter.GuidAdapter;
import com.yishuqiao.dto.FindDTO;
import com.yishuqiao.dto.FindDTO.ItemImageUrlListOriginal;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.photoview.PhotoView;
import com.yishuqiao.view.photoview.PhotoViewAttacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.yishuqiao.R.id.imageView;

public class ImageDetailsActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<View> viewList = new ArrayList<View>();
    private List<ItemImageUrlListOriginal> list = new ArrayList<FindDTO.ItemImageUrlListOriginal>();
    private TextView tv_num, tv_baocun;
    private int pager = 1;

    public static String FindItem = "FindItem";
    public static String PAGER_INDEX = "index";

    private int index = 0;
    RequestQueue mQueue;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedetails);
        init();
        verifyStoragePermissions(this);
    }
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("InflateParams")
    @SuppressWarnings({"unchecked", "deprecation"})
    public void init() {
        mQueue = Volley.newRequestQueue(this);
        tv_baocun = (TextView) findViewById(R.id.tv_baocun);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        list = (ArrayList<ItemImageUrlListOriginal>) getIntent().getSerializableExtra(FindItem);
        Log.e("zsy", "listzsy=" + list.size());
        LayoutInflater inflater = LayoutInflater.from(ImageDetailsActivity.this);
        for (int i = 0; i < list.size(); i++) {
            View view_1 = inflater.inflate(R.layout.view_imagedetails, null);
            TextView tv_content = (TextView) view_1.findViewById(R.id.tv_content);
            tv_content.setText(list.get(i).getTxt_other());
            PhotoView photoView = (PhotoView) view_1.findViewById(R.id.im_details);

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                @Override
                public void onPhotoTap(View view, float x, float y) {

                }

                @Override
                public void onOutsidePhotoTap() {
                    finish();
                }
            });

            final ProgressBar progressBar = (ProgressBar) view_1.findViewById(R.id.progressBarLoad);
            progressBar.setVisibility(View.VISIBLE);

            Picasso.with(ImageDetailsActivity.this).load(list.get(i).getTxt_url()).error(R.drawable.icon_error)
                    .into(photoView, new Callback() {

                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            // Utils.showToast(ImageDetailsActivity.this, "图片下载失败");
                        }
                    });

            viewList.add(view_1);
        }

        viewPager.setAdapter(new GuidAdapter(viewList));
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_num.setText(pager + "/" + list.size());
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // arg0是当前选中的页面的Position
                tv_num.setText((arg0 + 1) + "/" + list.size());
                index = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // arg0 :当前页面，及你点击滑动的页面；arg1:当前页面偏移的百分比；arg2:当前页面偏移的像素位置
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // arg0 ==1的时表示正在滑动，arg0==2的时表示滑动完毕了，arg0==0的时表示什么都没做。

            }
        });

        if (getIntent().hasExtra(PAGER_INDEX)) {
            index = getIntent().getIntExtra(PAGER_INDEX, 0);
        }

        viewPager.setCurrentItem(index);
        tv_baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageRequest imageRequest = new ImageRequest(
                        list.get(index).getTxt_url(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                Log.e("zsy","zzzzz=");
                                if (Utils.saveImageToGallery(ImageDetailsActivity.this,response,System.currentTimeMillis())){
                                    Toast.makeText(ImageDetailsActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(ImageDetailsActivity.this,"网络连接失败,请重试",Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                mQueue.add(imageRequest);
            }
        });
    }
}
