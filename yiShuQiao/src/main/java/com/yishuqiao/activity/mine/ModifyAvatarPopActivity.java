package com.yishuqiao.activity.mine;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.yishuqiao.R;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.utils.Utils;

/**
 * 作者：admin
 * <p>
 * 创建时间：2017-7-5 下午5:03:41
 * <p>
 * 项目名称：YiShuQiao
 * <p>
 * 文件名称：ModifyAvatarPopActivity.java
 * <p>
 * 类说明：修改头像
 */
public class ModifyAvatarPopActivity extends BaseActivity implements
        OnClickListener {
    private TextView takePhotoBtn = null;
    private TextView lookPhotoBtn = null;
    private TextView cancelBtn = null;
    private final int CAMERA_PHOTO = 2; // 拍摄的照片
    private final int SDC_PHOTO = 3; // 从SD卡上获得照片
    private final int CROP = 4;// 剪裁
    private Uri outUri = null; // 输入拍照头像的uri
    private Uri resUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        // 设置宽度全屏
        getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        takePhotoBtn = (TextView) findViewById(R.id.takePhotoBtn);
        lookPhotoBtn = (TextView) findViewById(R.id.lookPhotoBtn);
        cancelBtn = (TextView) findViewById(R.id.cancel);
        takePhotoBtn.setOnClickListener(this);
        lookPhotoBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            File dir = new File(Utils.CACHEPATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = Utils.CACHEPATH + System.currentTimeMillis()
                    + ".jpg";
            File file = new File(filename);
            resUri = Uri.fromFile(file);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhotoBtn:
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // sd card 可用
                } else {
                    // 当前不可用
                    Toast.makeText(ModifyAvatarPopActivity.this, "没有sdcard!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                initTakePhoto();
                break;
            case R.id.lookPhotoBtn:
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, SDC_PHOTO);
                break;
            case R.id.cancel:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化照相机
     */
    public void initTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File fileDir = new File(Utils.CACHEPATH);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String path = Utils.CACHEPATH + "person_avatar.jpg";
        File file = new File(path);
        outUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        startActivityForResult(intent, CAMERA_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CAMERA_PHOTO == requestCode && resultCode == RESULT_OK) {
            startPhotoCrop(outUri, resUri);
        } else if (SDC_PHOTO == requestCode && resultCode == RESULT_OK) {
            // 获得图片的uri
            if (data == null)
                return;
            Uri originalUri = data.getData();

            Uri uri = Uri.fromFile(new File(Utils.getPhotoPath(
                    ModifyAvatarPopActivity.this, originalUri)));
            startPhotoCrop(uri, resUri);
        } else if (CROP == requestCode && resultCode == RESULT_OK) {
            if (data != null) {
                Intent resultData = new Intent();
                resultData.setData(resUri);
                setResult(RESULT_OK, resultData);
                finish();
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    private void startPhotoCrop(Uri uri, Uri resultUri) {

        Intent intent = Utils.getCropImageIntent(uri, 200, 200, resultUri);
        startActivityForResult(intent, CROP);
    }
}
