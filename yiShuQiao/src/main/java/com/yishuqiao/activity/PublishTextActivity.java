package com.yishuqiao.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.yishuqiao.R;
import com.yishuqiao.adapter.ImagePublishAdapter;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.ImageItem;
import com.yishuqiao.dto.UploadDTO;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.CompressPictureUtil;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.CustomConstants;
import com.yishuqiao.utils.IntentConstants;
import com.yishuqiao.utils.PictureFileUtils;
import com.yishuqiao.utils.PictureUtil;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;
import com.yishuqiao.view.MyGridView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxParams;

public class PublishTextActivity extends BaseActivity {

    private EditText content;
    private MyGridView mGridView;
    private ImagePublishAdapter mAdapter;
    private RelativeLayout rl_public;// 发布按钮

    private LinearLayout layout;// 跟布局

    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();

    private CustomProgressDialog progressDialog = null;

    private TextView loadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_publishtext);

        // 当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }

        initData();
        initView();

    }

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
        saveTempToPref();// 保存上次选择的图片
        Conn.PublishMessage = content.getText().toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveTempToPref();
    }

    private void saveTempToPref() {
        SharedPreferences sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(mDataList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

    }

    private void getTempFromPref() {
        SharedPreferences sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr)) {
            List<ImageItem> tempImages = JSON.parseArray(prefStr, ImageItem.class);
            mDataList = tempImages;
        }
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null) {
            mDataList.addAll(incomingDataList);
        }
    }

    @Override
    protected void onResume() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        super.onResume();
        notifyDataChanged(); // 当在ImageZoomActivity中删除图片时，返回这里需要刷新

        content.setText(Conn.PublishMessage);
    }

    public void initView() {

        setResult(RESULT_OK);

        View view = progressDialog.getLoadView();
        loadMessage = (TextView) view.findViewById(R.id.textview_message);

        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText("发布图文");

        layout = (LinearLayout) findViewById(R.id.layout);

        rl_public = (RelativeLayout) findViewById(R.id.rl_public);
        rl_public.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 先影藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (TextUtils.isEmpty(content.getText())) {
                    Utils.showToast(PublishTextActivity.this, "请输入文字信息");
                } else if (mDataList.isEmpty()) {
                    Utils.showToast(PublishTextActivity.this, "请选择图片");
                } else {
                    // getSmallBitmap(v, 0);//质量压缩 会失真
                    compressPicture(v, 0);// 减低分辨率 不会失真
                }
            }

        });

        content = (EditText) findViewById(R.id.content);
        mGridView = (MyGridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == getDataSize()) {

                    Intent intent = new Intent(PublishTextActivity.this, PublishTextPopActivity.class);
                    startActivityForResult(intent, 1);

                    // new PopupWindows(PublishTextActivity.this, mGridView);

                } else {
                    Intent intent = new Intent(PublishTextActivity.this, ImageZoomActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST, (Serializable) mDataList);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivity(intent);
                }
            }
        });
        // sendTv = (TextView) findViewById(R.id.action);
        // sendTv.setText("发送");
        // sendTv.setOnClickListener(new OnClickListener()
        // {
        //
        // public void onClick(View v)
        // {
        // removeTempFromPref();
        // System.exit(0);
        // //TODO 这边以mDataList为来源做上传的动作
        // }
        // });
    }

    private int getDataSize() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private int getAvailableSize() {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mDataList.size();
        if (availSize >= 0) {
            return availSize;
        }
        return 0;
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.item_popupwindow, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

            setWidth(LayoutParams.MATCH_PARENT);
            setHeight(LayoutParams.WRAP_CONTENT);

            // setBackgroundDrawable(new ColorDrawable(0xb0000000));

            setTouchable(true);
            setFocusable(true);
            setBackgroundDrawable(new BitmapDrawable());
            setOutsideTouchable(true);
            setTouchInterceptor(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE && !isFocusable()) {
                        // 如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                        // 不做任何响应,不 dismiss popupWindow
                        dismiss();
                        return true;
                    }
                    // 否则default，往下dispatch事件:关掉popupWindow，
                    return false;
                }
            });

            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);

            update();

            Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    takePhoto();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(PublishTextActivity.this, ImageBucketChooseActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE, getAvailableSize());
                    startActivity(intent);
                    dismiss();
                    finish();
                }
            });
            bt3.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File vFile = new File(Environment.getExternalStorageDirectory() + "/myimage/",
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        path = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE && resultCode == -1 && !TextUtils.isEmpty(path)) {
                    ImageItem item = new ImageItem();
                    item.sourcePath = path;
                    mDataList.add(item);
                }
                break;

            case 1:

                if (resultCode == -1 && (data.getStringExtra("choose")).equals("Camera")) {
                    // 相机
                    takePhoto();
                } else if (resultCode == -1 && (data.getStringExtra("choose")).equals("Album")) {
                    // 相册
                    Intent intent = new Intent(PublishTextActivity.this, ImageBucketChooseActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE, getAvailableSize());
                    startActivity(intent);
                    finish();
                }

                break;

            default:
                break;
        }

    }

    private void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @param v
     * @param index
     * @Description (上传之前做压缩处理)
     */
    private void compressPicture(View v, final int index) {

        if (index == 0) {
            progressDialog.setTouchAble(false);
            progressDialog.show();
            v.setEnabled(false);
        }

        if (mDataList != null && mDataList.size() > index) {

            Bitmap saveBitmap = CompressPictureUtil.getSmallBitmap(mDataList.get(index).sourcePath, 1280, 720);// 上传服务器的bitmap
            PictureFileUtils.saveBitmap(saveBitmap, index + "");

            String targetPath = PictureFileUtils.SDPATH + "/" + index + ".jpg";

            final File compressedPic = new File(targetPath);
            if (compressedPic.exists()) {
                Log.e("图片压缩上传", "压缩图片");
                // Utils.showToast(this, "压缩图片");
                mDataList.get(index).sourcePath = targetPath;
            } else {// 直接上传
                Log.e("图片压缩上传", "原图图片");
                // Utils.showToast(this, "原图图片");
            }

            compressPicture(v, index + 1);

        } else {
            commit(v);
        }

    }

    /**
     * @param v
     * @param index
     * @Description (上传之前做压缩处理 图片质量会模糊)
     */
    private void getSmallBitmap(View v, final int index) {

        if (index == 0) {
            progressDialog.setTouchAble(false);
            progressDialog.show();
            v.setEnabled(false);
        }

        if (mDataList != null && mDataList.size() > index) {

            String targetPath = Conn.sdCardPathDown + "/picture" + index + ".jpg";
            // 调用压缩图片的方法，返回压缩后的图片path
            final String compressImage = PictureUtil.compressImage(mDataList.get(index).sourcePath, targetPath, 0);
            final File compressedPic = new File(compressImage);
            if (compressedPic.exists()) {
                Log.e("图片压缩上传", "压缩图片");
                mDataList.get(index).sourcePath = targetPath;
            } else {// 直接上传
                Log.e("图片压缩上传", "原图图片");
            }

            getSmallBitmap(v, index + 1);

        } else {
            commit(v);
        }

    }

    // 提交信息
    private void commit(final View view) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_cate", "2");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                // if (progressDialog != null && progressDialog.isShowing()) {
                // progressDialog.dismiss();
                // }

                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    uploadImg(dto.getUptoken(), 0, view);

                } else {
                    Utils.showToast(PublishTextActivity.this, dto.getTxt_message());
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    view.setEnabled(true);
                }
            }

            @Override
            public void start() {
                // progressDialog.setTouchAble(false);
                // progressDialog.show();
                view.setEnabled(false);
            }

            @Override
            public void error() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(PublishTextActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);

    }

    private void uploadImg(final String token, final int index, final View view) {

        // 设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("x:phone", "12345678");

        if (mDataList != null && mDataList.size() > index) {
            Uri uri = Uri.fromFile(new File(mDataList.get(index).sourcePath));
            MyApplication.getConfiguration().put(Utils.getRealFilePath(PublishTextActivity.this, uri), Utils.imgName(),
                    token, new UpCompletionHandler() {

                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {

                            Log.e("zsy", key + ",\r\n " + info + ",\r\n " + res);

                            // info.error中包含了错误信息，可打印调试
                            // 上传成功后将key值上传到自己的服务器
                            if (info.isOK()) {

                                mDataList.get(index).key = key;
                                uploadImg(token, index + 1, view);

                            } else {
                                Toast.makeText(PublishTextActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                                view.setEnabled(true);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                return;
                            }

                        }

                    }, new UploadOptions(null, null, false, new UpProgressHandler() {

                        @Override
                        public void progress(String key, double percent) {
                            Log.i("qiniu", key + ": " + percent);
                            long i = Math.round(percent * 100);
                            loadMessage.setText("第" + (index + 1) + "张" + i + "%");
                        }
                    }, null));
        } else {
            submitKey(view);
            // delFile();// 删除缓存的压缩图片
        }

    }

    private void delFile() {

        for (int index = 0; index < mDataList.size(); index++) {
            String targetPath = "/" + index + ".jpg";
            PictureFileUtils.delFile(targetPath);
        }

    }

    private void submitKey(final View view) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("find_type", "1");
        ajaxParams.put("find_detail", content.getText().toString());
        String key = "";
        for (int i = 0; i < mDataList.size(); i++) {
            key = key + mDataList.get(i).key + ",";
        }
        ajaxParams.put("find_img", key);
        Log.e("key = ", key);
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {

                view.setEnabled(true);

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    Utils.showToast(PublishTextActivity.this, "发布成功");

                    removeTempFromPref();
                    content.setText("");
                    Conn.PublishMessage = "";
                    mDataList.clear();

                    PublishTextActivity.this.finish();

                } else {
                    Utils.showToast(PublishTextActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
                // progressDialog.setTouchAble(false);
                // progressDialog.show();
                // view.setEnabled(false);
            }

            @Override
            public void error() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(PublishTextActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);

    }

}
