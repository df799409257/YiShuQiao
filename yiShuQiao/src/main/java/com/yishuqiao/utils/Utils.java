package com.yishuqiao.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.yishuqiao.R;
import com.yishuqiao.activity.mine.LoginActivity;
import com.yishuqiao.app.MyApplication;
import com.yishuqiao.view.MyDialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Utils {

    private static MyDialog myDialog;

    public static final String CACHEPATH = Environment.getExternalStorageDirectory() + File.separator + "yishuqiao"
            + File.separator;

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    // 启动自定义dialog
    public static void showDialog(Activity context, String title, String content, String leftText, String rightText,
                                  View.OnClickListener onClickListener) {
        myDialog = new MyDialog(context, R.style.my_dialog, title, content, leftText, rightText, onClickListener);
        // myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
    }

    // 销毁dialog
    public static void dismissDialog() {
        if (myDialog.isShowing())
            myDialog.dismiss();
    }

    /**
     * 返回字符串长度
     *
     * @param str
     * @return
     */
    public static int strCharacterCount(String str) {
        int icount = 0;
        String chinese = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
        Pattern pattern = Pattern.compile(chinese);
        int ilenth = str.length();
        for (int i = 0; i < ilenth; i++) {
            String substr = str.substring(i, i + 1);
            boolean tf = pattern.matcher(substr).matches();
            if (tf == true) {
                icount += 2;
            } else {
                icount++;
            }
        }
        // icount = icount / 2;
        return icount;
    }

    /**
     * 作者:admin 时间:2017-7-5 下午4:21:09 描述:判断是否登录
     */
    public static boolean checkAnony(Context context) {
        if (!(MyApplication.MySharedPreferences.readIsLogin())) {
            Intent it = new Intent(context, LoginActivity.class);
            context.startActivity(it);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 调用图片剪辑程序
     *
     * @param photoUri 被剪切图片的uri
     * @param outputY  图片输出X的尺寸
     * @param outputX  图片输出Y的尺寸
     * @return
     */
    public static Intent getCropImageIntent(Uri photoUri, int outputX, int outputY, Uri outUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        // 输出图片大小
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        // intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        return intent;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPhotoPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based
     * ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static int dipTopx(float dipValue, float scale) {
        return (int) (dipValue * scale + 0.5f);
    }

    public static String formatQiNiuURLForBanner(final ImageView imageView, final int width, String url) {
        url += "?imageView2/0/w/" + width + "/h/" + imageView.getMeasuredHeight();
        return url;
    }

    /**
     * 作者:admin 时间:2017年7月21日 下午4:28:46 描述:将秒数转化为时分秒
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ImageColumns.DATA}, null, null,
                    null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 判断返回数据是否为json
     *
     * @param json
     * @return
     */
    public static boolean isGson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static String chageStar(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return pNumber;
        }
    }

    // 自定义文件名称
    public static String imgName() {
        return "android" + System.currentTimeMillis();
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String getVideoPathTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * @return
     * @Description (判断是否是视频文件)
     */
    public static boolean isVideo(File file) {

        String fileName = file.getName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (prefix.equals("mp4")) {
            return true;
        } else if (prefix.equals("3gp")) {
            return true;
        } else if (prefix.equals("rmvb")) {
            return true;
        } else if (prefix.equals("avi")) {
            return true;
        } else if (prefix.equals("rm")) {
            return true;
        } else if (prefix.equals("MPEG")) {
            return true;
        } else if (prefix.equals("MPG")) {
            return true;
        } else if (prefix.equals("MOV")) {
            return true;
        } else if (prefix.equals("WMV ")) {
            return true;
        } else {
            return false;
        }

    }
    public static Process clearAppUserData(String packageName) {
        Process p = execRuntimeProcess("pm clear " + packageName);
        return p;
    }
    public static Process execRuntimeProcess(String commond) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(commond);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp,Long name) {
        // 首先保存图片
        String storePath = Conn.sdCardPathDown+"/";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {

            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
//            try {
//                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
