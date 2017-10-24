package com.yishuqiao.wxapi;

import org.greenrobot.eventbus.EventBus;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yishuqiao.activity.BaseActivity;
import com.yishuqiao.event.EventTypeString;
import com.yishuqiao.utils.Conn;
import com.yishuqiao.utils.ListViewDataUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    @SuppressWarnings("unused")
    private SharedPreferences spf;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        spf = getSharedPreferences("user", this.MODE_PRIVATE);
        api = WXAPIFactory.createWXAPI(this, Conn.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        // TODO Auto-generated method stub
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                Toast.makeText(this, "1", Toast.LENGTH_LONG).show();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Toast.makeText(this, "2", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ListViewDataUtils.saveShare(Integer.parseInt(ListViewDataUtils.readShare()) + 1 + "");
                Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                EventTypeString msg = new EventTypeString();
                msg.setMessage("shareSuccess");
                EventBus.getDefault().post(msg);
                finish();
                // Constant.WECHAT_CODE=sendResp.code;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);

        // finish();
    }
}
