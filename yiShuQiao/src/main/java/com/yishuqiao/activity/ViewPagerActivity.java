package com.yishuqiao.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yishuqiao.R;
import com.yishuqiao.dto.ViewPagerDTO;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.HackyViewPager;
import com.yishuqiao.view.photoview.PhotoView;
import com.yishuqiao.view.photoview.PhotoViewAttacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPagerActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    public static final String IMGS = "IMGS";
    public static final String INDEX = "INDEX";

    private ViewPager mViewPager;
    private ArrayList<ViewPagerDTO> imgs = null;

    public static String PAGER_INDEX = "index";

    private int index = 0;
    RequestQueue mQueue;
    private TextView tv_num,tv_baocun;

    private int pager = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mQueue = Volley.newRequestQueue(this);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        // setContentView(mViewPager);
        imgs = ((ArrayList<ViewPagerDTO>) getIntent().getSerializableExtra(IMGS));
        index = getIntent().getIntExtra(INDEX, 0);
        mViewPager.setAdapter(new SamplePagerAdapter(this, imgs));
        tv_baocun= (TextView) findViewById(R.id.tv_baocun);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

        if (getIntent().hasExtra(PAGER_INDEX)) {
            index = getIntent().getIntExtra(PAGER_INDEX, 0);
        }

        mViewPager.setCurrentItem(index);

        tv_num = (TextView) findViewById(R.id.tv_num);
        if (imgs.size() > 1) {
            tv_num.setText(pager + "/" + imgs.size());
        }
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                tv_num.setText((arg0 + 1) + "/" + imgs.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        verifyStoragePermissions(this);
        tv_baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageRequest imageRequest = new ImageRequest(
                        imgs.get(index).getPic_url(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                Log.e("zsy","zzzzz=");
                                if (Utils.saveImageToGallery(ViewPagerActivity.this,response,System.currentTimeMillis())){
                                    Toast.makeText(ViewPagerActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(ViewPagerActivity.this,"网络连接失败,请重试",Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class SamplePagerAdapter extends PagerAdapter {

        private Context context = null;
        private List<ViewPagerDTO> sDrawables = null;

        public SamplePagerAdapter(Context context, final List<ViewPagerDTO> sDrawables) {
            this.context = context;
            this.sDrawables = sDrawables;
        }

        @Override
        public int getCount() {
            return sDrawables.size();
        }

        @SuppressLint("InflateParams")
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.imgView);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setText(sDrawables.get(position).getTxt_content());
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                @Override
                public void onPhotoTap(View view, float x, float y) {

                }

                @Override
                public void onOutsidePhotoTap() {
                    ((Activity) context).finish();
                }
            });
            // ImageView imageView = (ImageView)
            // view.findViewById(R.id.imgView);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarLoad);
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(context).load(sDrawables.get(position).getPic_url()).into(photoView, new Callback() {

                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);
                    Utils.showToast(context, "图片下载失败");
                }
            });

            // // Now just add PhotoView to ViewPager and return it
            // container.addView(photoView, LayoutParams.MATCH_PARENT,
            // LayoutParams.MATCH_PARENT);
            container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @SuppressWarnings("unused")
    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            ((HackyViewPager) mViewPager).toggleLock();
        }
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

}
