package com.yishuqiao.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.yishuqiao.R;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.dto.UploadDTO;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.http.HttpNetWork;
import com.yishuqiao.http.HttpRusult;
import com.yishuqiao.http.HttpUrl;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.Utils;
import com.yishuqiao.view.CustomProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxParams;

public class PublishVideoActivity extends BaseActivity {

    private ImageView im_fengmian;

    public static String FILEPATH = "path";
    public static String ISLOCAL = "local";
    private String is_local = "0";

    private TextView tv_title;
    private RelativeLayout rl_public;

    private String filePath = null;

    private String videoWidth = "1280";
    private String videoHeight = "720";

    private CustomProgressDialog progressDialog = null;

    private EditText content;

    private String picKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_publicvideo);

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
        // com = new Compressor(this);
        initView();
    }

    private void initView() {

        // comPress("", "", null);
        if (getIntent().hasExtra(FILEPATH)) {
            filePath = getIntent().getStringExtra(FILEPATH);
        }

        if (getIntent().hasExtra(ISLOCAL)) {
            is_local = getIntent().getStringExtra(ISLOCAL);
        }

        if (filePath == null) {
            finish();
        }

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(filePath);
        Bitmap bitmap = media.getFrameAtTime();
        if (bitmap != null) {
            videoWidth = bitmap.getWidth() + "";
            videoHeight = bitmap.getHeight() + "";
        }

        // 保存缩视频略图到本地
        try {
            saveBitmap(bitmap, "videoPic.png", Environment.getExternalStorageDirectory().toString() + "/YSQ/");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("发布视频");

        rl_public = (RelativeLayout) findViewById(R.id.rl_public);
        rl_public.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 先影藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (TextUtils.isEmpty(content.getText())) {
                    Utils.showToast(PublishVideoActivity.this, "请输入文字信息");
                } else if (filePath == null) {
                    Utils.showToast(PublishVideoActivity.this, "请选择视频");
                } else {
                    progressDialog.show();
                    getToken(v, filePath);
                    getPicToken();
                }
            }

        });

        content = (EditText) findViewById(R.id.content);

        im_fengmian = (ImageView) findViewById(R.id.im_fengmian);
        im_fengmian.setImageBitmap(getVideoThumbnail(filePath));
        im_fengmian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // startActivity(new Intent(PublishVideoActivity.this, PreviewVideoActivity.class)
                // .putExtra(PreviewVideoActivity.FILEPATH, filePath));
//                startActivity(
//                        new Intent(PublishVideoActivity.this, VideoPlayerActivity.class).putExtra("path", filePath));
            }
        });

    }

    // 获取上传凭证
    private void getPicToken() {

        AjaxParams ajaxParams = new AjaxParams();

        ajaxParams.put("type", "0");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_cate", "3");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    if (getIntent().hasExtra("framePicPath")) {
                        commitPic(
                                Uri.fromFile(new File(
                                        getIntent().getStringExtra("framePicPath"))),
                                dto.getUptoken());
                    } else {
                        commitPic(
                                Uri.fromFile(new File(
                                        Environment.getExternalStorageDirectory().toString() + "/YSQ/" + "videoPic.png")),
                                dto.getUptoken());
                    }
                } else {
                    Utils.showToast(PublishVideoActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {

            }

            @Override
            public void error() {
                Utils.showToast(PublishVideoActivity.this, "网络连接失败");
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);
    }

    // 获取上传凭证
    private void getToken(final View view, final String filepath) {

        AjaxParams ajaxParams = new AjaxParams();

        if (is_local.equals("1")) {
            ajaxParams.put("type", "9");
            ajaxParams.put("videoHeight", videoHeight);
            ajaxParams.put("videoWidth", videoWidth);
            Log.e("视频宽高 ", videoWidth + "*" + videoHeight);
        } else {
            ajaxParams.put("type", "0");
        }

        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("txt_cate", "3");
        HttpNetWork httpNetWork = new HttpNetWork(new HttpRusult() {

            @Override
            public void succ(String t) {
                UploadDTO dto = new UploadDTO();
                if (Conn.IsNetWork) {
                    dto.setTxt_code("0");
                    dto.setTxt_message("上传成功");
                } else {
                    Gson gson = new Gson();
                    dto = gson.fromJson(t, UploadDTO.class);
                }
                if (dto.getTxt_code().equals("0")) {

                    View view = progressDialog.getLoadView();
                    TextView loadMessage = (TextView) view.findViewById(R.id.textview_message);
                    uploadVideo(dto.getUptoken(), filepath, view, loadMessage);

                } else {
                    Utils.showToast(PublishVideoActivity.this, dto.getTxt_message());
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    view.setEnabled(true);
                }
            }

            @Override
            public void start() {
                progressDialog.setTouchAble(false);
                progressDialog.show();
                view.setEnabled(false);
            }

            @Override
            public void error() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.showToast(PublishVideoActivity.this, "网络连接失败");
                view.setEnabled(true);
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);
    }

    private void commitPic(Uri uri, String token) {

        MyApplication.getConfiguration().put(Utils.getRealFilePath(PublishVideoActivity.this, uri), Utils.imgName(),
                token, new UpCompletionHandler() {

                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {

                        Log.e("zsy", key + ",\r\n " + info + ",\r\n " + res);

                        // info.error中包含了错误信息，可打印调试
                        // 上传成功后将key值上传到自己的服务器
                        if (info.isOK()) {

                            picKey = key;

                        } else {
                            Toast.makeText(PublishVideoActivity.this, "视频缩略图片上传失败", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                }, null);
    }

    private void uploadVideo(final String uptoken, String filepath, final View view, final TextView loadMessage) {
        MyApplication.getConfiguration().put(filepath, Utils.imgName(), uptoken, new UpCompletionHandler() {

            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                // 上传成功后将key值上传到自己的服务器
                if (info.isOK()) {

                    submitKey(key, view);

                } else {
                    Toast.makeText(PublishVideoActivity.this, "视频上传失败", Toast.LENGTH_SHORT).show();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    view.setEnabled(true);
                    return;
                }
                Log.e("zsy", key + ",\r\n " + info + ",\r\n " + res);

            }

        }, new UploadOptions(null, null, false, new UpProgressHandler() {

            @Override
            public void progress(String key, double percent) {
                Log.i("qiniu", key + ": " + percent);
                long i = Math.round(percent * 100);
                loadMessage.setText("进度" + i + "%");
            }
        }, null));

    }

    private void submitKey(String key, final View view) {

        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("type", "1");
        ajaxParams.put("txt_userid", MyApplication.MySharedPreferences.readUserid());
        ajaxParams.put("txt_usertoken", MyApplication.MySharedPreferences.readToken());
        ajaxParams.put("find_type", "2");
        ajaxParams.put("find_detail", content.getText().toString());
        ajaxParams.put("find_img", key);
        ajaxParams.put("find_cover", picKey);
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

                    Toast.makeText(PublishVideoActivity.this, "视频上传成功", Toast.LENGTH_SHORT).show();
                    PublishVideoActivity.this.setResult(RESULT_OK);

                    EventTypeString msg = new EventTypeString();
                    msg.setMessage("public");
                    EventBus.getDefault().post(msg);

                    finish();
                } else {
                    Utils.showToast(PublishVideoActivity.this, dto.getTxt_message());
                }
            }

            @Override
            public void start() {
                progressDialog.setTouchAble(false);
                progressDialog.show();
                view.setEnabled(false);
            }

            @Override
            public void error() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(PublishVideoActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }

        }, this);
        httpNetWork.postHttp(ajaxParams, HttpUrl.Upload);

    }

    public Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    /**
     * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;

    }

    /**
     * @param videoPath
     * @return
     * @Description (获得本地视频缩略图)
     */
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

    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(this);
        }
        super.onResume();
    }

    // public void comPress(final String oldpath,final String newpath,final View v){
    // final String cmd= "-y -i /mnt/shared/Other/1502072826465.314941iphone.mp4 -strict -2 -vcodec libx264 -preset
    // ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x352 -aspect 16:9 /mnt/shared/Other/4.mp4";
    // com.loadBinary(new InitListener() {
    // @Override
    // public void onLoadSuccess() {
    // com.execCommand(cmd,new CompressListener() {
    // @Override
    // public void onExecSuccess(String message) {
    // Log.e("zsy","succ="+message);
    //// getToken(v, Uri.fromFile(new File(newpath)));
    // progressDialog.dismiss();
    // }
    //
    // @Override
    // public void onExecFail(String reason) {
    // Log.e("zsy","fail="+reason);
    // Toast.makeText(PublishVideoActivity.this, "视频压缩失败,请重试", 300).show();
    // progressDialog.dismiss();
    // }
    //
    // @Override
    // public void onExecProgress(String message) {
    // Log.i("zsy","progress"+message);
    //
    // }
    // });
    // }
    //
    // @Override
    // public void onLoadFail(String reason) {
    // Log.e("zsy","fail="+reason);
    // Toast.makeText(PublishVideoActivity.this, "视频压缩失败,请重试", 300).show();
    // progressDialog.dismiss();
    // }
    // });
    // }
    // public void commpree(String intpath,final String outpath,final View v){
    // Toast.makeText(PublishVideoActivity.this, "yasuo", 300).show();
    // File intfile=new File(intpath);
    // File outfile=new File(outpath);
    // cmd="ffmpeg -i "+intfile.getPath()+" -codec:v libx264 -codec:a mp3 -map 0 -f ssegment -segment_format mpegts
    // -segment_list playlist.m3u8 -segment_time 10 "+outfile.getPath();
    //// cmd="ffmpeg -i "+intfile.getPath()+" -vcodec libx264 -preset fast -crf 20 -y -vf scale=1920:-1 -acodec
    // libmp3lame -ab 128k "+intfile.getPath();
    //// cmd = "-y -i "+intfile.getPath()+" -strict -2 -vcodec libx264 -preset ultrafast -crf 40 -acodec aac -ar 44100
    // -ac 2 -b:a 96k -s 640x352 -aspect 16:9 "+outfile.getPath();
    // com = new Compressor(this);
    //
    // com.loadBinary(new InitListener() {
    // @Override
    // public void onLoadSuccess() {
    // com.execCommand(cmd,new CompressListener() {
    // @Override
    // public void onExecSuccess(String message) {
    // Log.e("zsy","success="+message);
    // progressDialog.dismiss();
    // getToken(v, Uri.fromFile(new File(outpath)));
    // Toast.makeText(PublishVideoActivity.this, "success="+message, 300).show();
    // }
    //
    // @Override
    // public void onExecFail(String reason) {
    // Log.e("zsy","fail="+reason);
    // progressDialog.dismiss();
    // Toast.makeText(PublishVideoActivity.this, "fail="+reason, 300).show();
    // }
    //
    // @Override
    // public void onExecProgress(String message) {
    // Log.e("zsy","progress="+message);
    // progressDialog.show();
    // Toast.makeText(PublishVideoActivity.this, "progress="+message, 300).show();
    // }
    // });
    // }
    //
    // @Override
    // public void onLoadFail(String reason) {
    // Log.e("zsy","fail="+reason);
    // progressDialog.dismiss();
    // Toast.makeText(PublishVideoActivity.this, "fail="+reason, 300).show();
    // }
    // });

    // }
}
