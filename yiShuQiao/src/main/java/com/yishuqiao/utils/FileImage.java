package com.yishuqiao.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileImage {

    public static String path = Environment.getExternalStorageDirectory()
            .toString() + "/huayou/";

    public void saveFile(Bitmap bitmap, String imagename) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(CommonUtilImage.getPath() + imagename);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveMyBitmap(String bitName, Bitmap mBitmap)
            throws Exception {

        if (CommonUtilImage.hasSDCard()) {

            File f = getFilePath(path, bitName);

            FileOutputStream fOut = null;

            fOut = new FileOutputStream(f);

            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            Log.i("xiaoqiang", "image is save");

            fOut.flush();

            fOut.close();

        }
    }

    public static Bitmap useTheImage(String imagename) {

        return BitmapFactory.decodeFile(CommonUtilImage.getPath() + imagename);
    }

    public void execFile(String imageUrl) {

        String imageSDCardPath = CommonUtilImage.getPath() + imageUrl;
        File file = new File(imageSDCardPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {

            file = new File(filePath + fileName);
            Log.i("xiaoqiang", "new  yige  file" + file.getPath());
        } catch (Exception e) {

            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    public static Bitmap loadBitmap(String url, int pos) throws Exception {

        URL m;
        InputStream is = null;
        try {
            if (url != null) {
                m = new URL(url);
                // is = (InputStream) m.getContent();
                is = m.openStream();
                BufferedInputStream bis = null;
                if (is != null) {
                    bis = new BufferedInputStream(is);
                } else {

                    return null;
                }

                // 标记其实位置，供reset参�?
                // bis.mark(0);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                // true,只是读图片大小，不申请bitmap内存
                // opts.inJustDecodeBounds = true;
                // BitmapFactory.decodeStream(bis, null, opts);
                //
                // int size = (opts.outWidth * opts.outHeight);
                // int zoomRate = 0;
                // if( size > 600*600*4){
                // zoomRate = 16;
                // //zommRate缩放比，根据情况自行设定，如果为2则缩放为原来�?/2，如果为1不缩�?
                // opts.inSampleSize = zoomRate;
                // }else{
                //
                // opts.inSampleSize =2;
                // }
                opts.inSampleSize = pos;
                // 设为false，这次不是预读取图片大小，�?是返回申请内存，bitmap数据
                opts.inJustDecodeBounds = false;
                // 缓冲输入流定位至头部，mark()
                // bis.reset();
                Bitmap bm = BitmapFactory.decodeStream(bis, null, opts);

                bis.close();
                is.close();

                return (bm == null) ? null : bm;
            }
        } catch (MalformedURLException e1) {

            e1.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }
}
