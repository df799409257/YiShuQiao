package com.yishuqiao.activity.discover;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.activity.PreviewVideoActivity;
import com.yishuqiao.activity.PublishVideoActivity;
import com.yishuqiao.adapter.VideodetailListviewAdapter;
import com.yishuqiao.dto.BitmapEntity;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.refreshlayout.BGAMeiTuanRefreshViewHolder;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout;
import com.yishuqiao.view.refreshlayout.BGARefreshLayout.BGARefreshLayoutDelegate;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName VideoListActivity
 * @Description TODO(获取本地视频列表)
 * @Date 2017年8月10日 上午10:52:35
 */
public class VideoListActivity extends BaseActivity implements BGARefreshLayoutDelegate {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.listview)
    ListView listview;

    @Bind(R.id.content)
    RelativeLayout content;

    @Bind(R.id.swipe_container)
    BGARefreshLayout swipeRefreshLayout;

    private VideodetailListviewAdapter adapter;
    private List<BitmapEntity> bit = new ArrayList<BitmapEntity>();

    private String videoPath = "-1";
    private static final int VIDEO_CAPTURE = 0;
    private static final int VIDEO_SUCCESS = 1;
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_videolsit);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ButterKnife.bind(this);

        BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(this, true);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.drawable.bga_refresh_loading01);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.pull_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.pull_refreshing);
        swipeRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);
        swipeRefreshLayout.setPullDownRefreshEnable(true);
        swipeRefreshLayout.setDelegate(this);

        initView();

    }

    @SuppressLint("InflateParams")
    private void initView() {

        tv_title.setText("本地视频");

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -1);
        emptyParams.gravity = Gravity.CENTER;
        ((ViewGroup) listview.getParent()).addView(emptyView, emptyParams);
        listview.setEmptyView(emptyView);

        new Search_photo().start();

        showLoadingView();

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @SuppressLint("ShowToast")
        public void handleMessage(android.os.Message msg) {

            dismissLoadingView();

            for (int i = 0; i < bit.size(); i++) {
                if (bit.get(i).getBitmap() == null) {
                    bit.remove(i);
                }
            }

            if (msg.what == 1 && bit != null) {
                adapter = new VideodetailListviewAdapter(VideoListActivity.this, bit);
                // Toast.makeText(VideoListActivity.this, "视频总数:" + bit.size(), Toast.LENGTH_SHORT).show();
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        if (bit.size() != 0 && bit.get(arg2).getDuration() == 0) {
                            Toast.makeText(VideoListActivity.this, "请选择可以播放的视频", Toast.LENGTH_SHORT).show();
                        } else if (bit.size() != 0 && bit.get(arg2).getSize() > 10485760) {
                            Toast.makeText(VideoListActivity.this, "视频过大，请选择小于10M的视频", Toast.LENGTH_SHORT).show();
                        } else if (bit.size() != 0 && bit.get(arg2).getBitmap() == null) {
                            Toast.makeText(VideoListActivity.this, "视频无法播放请重新选择", Toast.LENGTH_SHORT).show();
                        } else {

                            videoPath = Utils.getRealFilePath(VideoListActivity.this,
                                    Uri.parse("file://" + bit.get(arg2).getUri()));

                            if (videoPath.equals("-1")) {
                                Toast.makeText(VideoListActivity.this, "视频发生错误", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivityForResult(new Intent(VideoListActivity.this, PublishVideoActivity.class)
                                        .putExtra(PublishVideoActivity.FILEPATH, videoPath), VIDEO_SUCCESS);
                            }

                        }

                        // Log.e("10 * 1024 * 1024", "10 * 1024 * 1024 = " + 10 * 1024 * 1024);
                        // Log.e("视频大小", "大小 = " + bit.get(arg2).getSize());
                        // Log.e("视频时长", "时长 = " + bit.get(arg2).getDuration());
                    }

                });
            }
        }

        ;
    };

    @OnClick(R.id.video)
    public void video(View view) {
        // startActivity(new Intent(this, PublishVideoActivity.class));
        // Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);// 限制录制时间10秒
        //// intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024 * 10L);
        // startActivityForResult(intent, VIDEO_CAPTURE);
//        Intent intent = new Intent(VideoListActivity.this, MediaRecorderActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_CAPTURE) {
            Uri uri = data.getData();
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(VideoColumns._ID));
                filePath = cursor.getString(cursor.getColumnIndex(VideoColumns.DATA));
                // imageView1.setImageBitmap(getVideoThumbnail(filePath));
                cursor.close();

                startActivityForResult(new Intent(VideoListActivity.this, PublishVideoActivity.class)
                        .putExtra(PreviewVideoActivity.FILEPATH, filePath), VIDEO_SUCCESS);

            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_SUCCESS) {
            setResult(RESULT_OK);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 遍历系统数据库找出相应的是视频的信息，每找出一条视频信息的同时利用与之关联的找出对应缩略图的uri 再异步加载缩略图， 由于查询速度非常快，全部查找完成在设置，等待时间不会太长
     *
     * @author Administrator
     */
    class Search_photo extends Thread {

        @Override
        public void run() {

            // 如果有sd卡（外部存储卡）
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = VideoListActivity.this.getApplicationContext().getContentResolver();
                Cursor cursor = cr.query(originalUri, null, null, null, null);
                if (cursor == null) {
                    return;
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    // 获取当前Video对应的Id，然后根据该ID获取其缩略图的uri
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String[] selectionArgs = new String[]{id + ""};
                    String[] thumbColumns = new String[]{MediaStore.Video.Thumbnails.DATA,
                            MediaStore.Video.Thumbnails.VIDEO_ID};
                    String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";

                    String uri_thumb = "";
                    Cursor thumbCursor = (VideoListActivity.this.getApplicationContext().getContentResolver()).query(
                            MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs,
                            null);

                    if (thumbCursor != null && thumbCursor.moveToFirst()) {
                        uri_thumb = thumbCursor
                                .getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));

                    }

                    // BitmapEntity bitmapEntity = new BitmapEntity(title, path, size, uri_thumb, duration);

                    BitmapEntity bitmapEntity = new BitmapEntity(
                            getVideoThumbnail(
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))),
                            title, path, size, duration);

                    bit.add(bitmapEntity);
                }

                if (cursor != null) {
                    cursor.close();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }
    }

    public Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        Log.e("zsy", "videoPath111=" + videoPath);
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    // 下拉刷新
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        showLoadingView();
        bit.clear();
        new Search_photo().start();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                swipeRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    // 上拉加载
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return true;
    }

}
