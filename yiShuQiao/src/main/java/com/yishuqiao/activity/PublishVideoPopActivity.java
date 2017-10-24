package com.yishuqiao.activity;

import com.yishuqiao.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PublishVideoPopActivity extends BaseActivity implements OnClickListener {

    private RelativeLayout chooseCameraBtn = null;
    private RelativeLayout chooseAlbumBtn = null;
    private LinearLayout cancelBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_video_popwindow);

        // 设置宽度全屏
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        chooseCameraBtn = (RelativeLayout) findViewById(R.id.chooseCameraBtn);
        chooseAlbumBtn = (RelativeLayout) findViewById(R.id.chooseAlbumBtn);
        cancelBtn = (LinearLayout) findViewById(R.id.cancelBtn);

        chooseCameraBtn.setOnClickListener(this);
        chooseAlbumBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseCameraBtn:
                setResult("Camera");
                break;
            case R.id.chooseAlbumBtn:
                setResult("Album");
                break;
            case R.id.cancelBtn:
                finish();
                break;
            default:
                break;
        }
    }

    private void setResult(final String choose) {
        Intent it = new Intent();
        it.putExtra("choose", choose);
        setResult(RESULT_OK, it);
        finish();
    }

}
