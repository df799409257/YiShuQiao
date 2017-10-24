package com.yishuqiao.activity.video.lib;

import com.yishuqiao.R;
import com.yishuqiao.activity.HomeActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class JCFullScreenActivity extends Activity {

    static void toActivityFromNormal(Context context, int state, String url, String title) {
        STATE = state;
        URL = url;
        TITLE = title;
        start = false;
        Intent intent = new Intent(context, JCFullScreenActivity.class);
        context.startActivity(intent);
    }

    public static void toActivity(Context context, String url, String title) {
        STATE = JCVideoPlayer.CURRENT_STATE_NORMAL;
        URL = url;
        TITLE = title;
        start = true;
        Intent intent = new Intent(context, JCFullScreenActivity.class);
        context.startActivity(intent);
    }

    JCVideoPlayer jcVideoPlayer;
    public static int STATE = -1;
    public static String URL;
    public static String TITLE;
    public static boolean manualQuit = false;
    static boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_fullscreen);

        jcVideoPlayer = (JCVideoPlayer) findViewById(R.id.jcvideoplayer);
        jcVideoPlayer.setUpForFullscreen(URL, TITLE);
        jcVideoPlayer.setState(STATE);
        manualQuit = false;
        if (start) {
            jcVideoPlayer.ivStart.performClick();
        } else {
            jcVideoPlayer.isFullscreenFromNormal = true;
            jcVideoPlayer.addSurfaceView();
            if (JCMediaManager.intance().listener != null) {
                JCMediaManager.intance().listener.onCompletion();
            }
            JCMediaManager.intance().listener = jcVideoPlayer;
        }
    }

    @Override
    public void onBackPressed() {
        JCVideoPlayer.isClickFullscreen = false;
        jcVideoPlayer.quitFullScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!manualQuit) {
            JCVideoPlayer.isClickFullscreen = false;
            JCVideoPlayer.releaseAllVideos();
            finish();
        }
    }

}
