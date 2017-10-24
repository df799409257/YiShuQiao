package com.yishuqiao.okhttp.callback;

import okhttp3.Response;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BitmapCallback extends Callback<Bitmap> {

    @Override
    public Bitmap parseNetworkResponse(Response response) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

}
