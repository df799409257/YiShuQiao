package com.yishuqiao.view;

import com.yishuqiao.R;
import com.yishuqiao.view.slidingmenu.SlidingMenu;
import com.yishuqiao.view.slidingmenu.app.SlidingFragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

    private Context mContext;
    private boolean isCanSliding = false;
    private View customView;

    public CustomProgressDialog(final Context context) {
        // TODO Auto-generated constructor stub
        super(context, R.style.customProgressDialog);
        this.mContext = context;

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);

        setTouchAble(true);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        customView = inflater.inflate(R.layout.custom_progress_dialog_layout, null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(customView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mContext instanceof SlidingFragmentActivity) {
            isCanSliding = ((SlidingFragmentActivity) mContext).getSlidingMenu()
                    .getTouchModeAbove() == SlidingMenu.TOUCHMODE_NONE ? false : true;
        }
        slidingMenuEnable((Activity) mContext, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        slidingMenuEnable((Activity) mContext, isCanSliding);
    }

    /**
     * 设置是否可点击下一层 true:可点击 false:不可点击 默认是可点击
     *
     * @param touchAble
     */
    public void setTouchAble(boolean isTouchAble) {

        if (isTouchAble) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            // getWindow().setFlags(0,
            // WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    /**
     * 设置侧滑是否可用
     *
     * @param activity  页面
     * @param isSliding true 可用 false 不可用
     */
    private void slidingMenuEnable(Activity activity, boolean isSliding) {
        if (activity instanceof SlidingFragmentActivity) {
            SlidingMenu sm = ((SlidingFragmentActivity) activity).getSlidingMenu();
            if (isSliding) {
                sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            } else {
                sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            }
        }
    }

    @Override
    public View findViewById(int id) {

        return super.findViewById(id);
    }

    public View getLoadView() {
        return customView;
    }

}
