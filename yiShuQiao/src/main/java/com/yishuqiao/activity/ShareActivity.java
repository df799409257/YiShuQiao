package com.yishuqiao.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yishuqiao.R;
import com.yishuqiao.dto.ShareDTO.ShareList;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;
import com.yishuqiao.utils.MyToast;

/**
 * @author Administrator
 * @version 1.0.0
 * @ClassName ShareUtil
 * @Description TODO(鍒嗕韩)
 * @Date 2017骞�7鏈�26鏃� 涓嬪崍1:51:10
 */

public class ShareActivity extends BaseActivity implements IWeiboHandler.Response {

    private IWXAPI api;

    private MyToast myToast = null;

    public static String SHARE_CONTENT = "SHARE_CONTENT";

    private ShareList shareList = null;

    private Bitmap shareIcon1 = null;

    private Bitmap shareIcon2 = null;

    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    private IWeiboShareAPI mWeiboShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);

        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        setFinishOnTouchOutside(true);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        api = WXAPIFactory.createWXAPI(this, Conn.APP_ID, true);
        api.registerApp(Conn.APP_ID);

        myToast = new MyToast(this);

        initView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            // 分享成功关闭页面
            finish();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ListViewDataUtils.saveShare(Integer.parseInt(ListViewDataUtils.readShare()) + 1 + "");
                    Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(this,
                            getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(ShareActivity.this, Conn.WEIBO_ID);
            mWeiboShareAPI.registerApp();
            sendMultiMessage();

        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareActivity.this, R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(ShareActivity.this, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {

        if (getIntent().hasExtra(SHARE_CONTENT)) {
            shareList = (ShareList) getIntent().getSerializableExtra(SHARE_CONTENT);
        }

    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    wechatShare(0, shareIcon1);
                    break;

                case 1:
                    wechatShare(1, shareIcon2);
                    break;

                default:
                    break;
            }

        }

        ;
    };

    // 寰俊鍒嗕韩
    @OnClick(R.id.weixin)
    public void weixin(View view) {

        // myToast.show("鍒嗕韩鎴愬姛");

        if (!TextUtils.isEmpty(shareList.getTxt_img())) {
            new Thread() {

                public void run() {
                    URL myFileUrl = null;
                    try {
                        myFileUrl = new URL(shareList.getTxt_img());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        shareIcon1 = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }

                ;
            }.start();

        } else {
            shareIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            wechatShare(0, shareIcon1);
        }

    }

    // 鏈嬪弸鍦堝垎浜�
    @OnClick(R.id.weixin_f)
    public void weixin_f(View view) {

        // myToast.show("鍒嗕韩鎴愬姛");

        if (!TextUtils.isEmpty(shareList.getTxt_img())) {

            new Thread() {

                public void run() {
                    URL myFileUrl = null;
                    try {
                        myFileUrl = new URL(shareList.getTxt_img());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        shareIcon2 = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }

                ;
            }.start();

        } else {
            shareIcon2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            wechatShare(1, shareIcon2);
        }

    }

    // 寰崥鍒嗕韩
    @OnClick(R.id.weibo)
    public void weibo(View view) {
        mAuthInfo = new AuthInfo(this, Conn.WEIBO_ID, Conn.REDIRECT_URL, Conn.SCOPE);
        mSsoHandler = new SsoHandler(ShareActivity.this, mAuthInfo);
        mSsoHandler.authorizeWeb(new AuthListener());
        // Toast.makeText(ShareActivity.this, "鏆備笉鏀寔姝ゅ姛鑳�", 300).show();
    }

    // private void sendMultiMessage(boolean hasText, boolean hasImage) {
    //
    //
    // WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
    // if (hasText) {
    // weiboMessage.textObject = getTextObj();
    // }
    // if (hasImage) {
    // weiboMessage.imageObject = getImageObj();
    // }
    //
    // shareHandler.shareMessage(weiboMessage, false);
    //
    // }
    // private TextObject getTextObj() {
    // TextObject textObject = new TextObject();
    // textObject.text = "娴嬭瘯鍐呭";
    // textObject.title = "娴嬭瘯鏍囬";
    // textObject.actionUrl = "http://www.baidu.com";
    // return textObject;
    // }
    // private ImageObject getImageObj() {
    // ImageObject imageObject = new ImageObject();
    // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    // imageObject.setImageObject(bitmap);
    // return imageObject;
    // }

    // private void sendMultiMessage(boolean hasText, boolean hasImage) {
    // WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
    // if (hasText) {
    // weiboMessage.textObject = getTextObj();
    // }
    // if (hasImage) {
    // weiboMessage.imageObject = getImageObj();
    // }
    //
    // shareHandler.shareMessage(weiboMessage, false);
    //
    // }
    //
    // private TextObject getTextObj() {
    // TextObject textObject = new TextObject();
    // textObject.text = "娴嬭瘯鍐呭";
    // textObject.title = "娴嬭瘯鏍囬";
    // textObject.actionUrl = "http://www.baidu.com";
    // return textObject;
    // }
    //
    // private ImageObject getImageObj() {
    // ImageObject imageObject = new ImageObject();
    // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    // imageObject.setImageObject(bitmap);
    // return imageObject;
    // }

    @Override
    protected void onPause() {

        if (myToast != null) {
            myToast.cancel();
        }

        super.onPause();
    }

    /**
     * 鍒嗕韩鍒板井淇�
     */
    private void wechatShare(int flag, Bitmap bitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareList.getTxt_url();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareList.getTxt_title();
        msg.description = shareList.getTxt_detail();
        msg.setThumbImage(bitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 缃戠粶鍥剧墖杞崲涓築itmap
     */
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 鎶婄綉缁滆祫婧愬浘鐗囪浆鍖栨垚bitmap
     *
     * @param url 缃戠粶璧勬簮鍥剧墖
     * @return Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {

        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {

            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    private void sendMultiMessage() {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.mediaObject = getWebpageObj();

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        mWeiboShareAPI.sendRequest(ShareActivity.this, request);

    }

    /**
     * 鍒涘缓澶氬獟浣擄紙缃戦〉锛夋秷鎭璞°��
     *
     * @return 澶氬獟浣擄紙缃戦〉锛夋秷鎭璞°��
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareList.getTxt_title();
        mediaObject.description = shareList.getTxt_detail();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 璁剧疆 Bitmap 绫诲瀷鐨勫浘鐗囧埌瑙嗛瀵硅薄閲� 璁剧疆缂╃暐鍥俱�� 娉ㄦ剰锛氭渶缁堝帇缂╄繃鐨勭缉鐣ュ浘澶у皬涓嶅緱瓒呰繃 32kb銆�
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = shareList.getTxt_url();
        mediaObject.defaultText = "Webpage";
        return mediaObject;
    }

    /**
     * @param msg
     * @Description (消息处理)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEent(EventTypeString msg) {
        String message = msg.getMessage();
        if (message.equals("shareSuccess")) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
