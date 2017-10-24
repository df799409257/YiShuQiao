package com.yishuqiao.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.yishuqiao.R;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class PreviewVideoActivity extends BaseActivity {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private File file;

    public static String FILEPATH = "filePath";

    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewvideo);
        init();
    }

    public void init() {

        if (getIntent().hasExtra(FILEPATH)) {
            filePath = getIntent().getStringExtra(FILEPATH);
        }

        // 保存缩视频略图到本地
        if (getVideoThumbnail(filePath) != null) {
            try {
                saveBitmap(getVideoThumbnail(filePath), "videoPic.png",
                        Environment.getExternalStorageDirectory().toString() + "/YSQ/");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        file = new File(filePath);
        surfaceView = (SurfaceView) findViewById(R.id.main_surface_view);
        mediaPlayer = new MediaPlayer();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // 准备完成后播放
                mediaPlayer.start();
            }
        });
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.setDataSource(PreviewVideoActivity.this, Uri.parse(file.getAbsolutePath()));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        });
        surfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.start();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void saveBitmap(Bitmap bm, String fileName, String subForder) throws IOException {
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    /**
     * @param videoPath
     * @return
     * @Description (获得本地视频缩略图)
     */
    private Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

}
