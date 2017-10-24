package com.yishuqiao.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class ColumnHorizontalScrollView extends HorizontalScrollView {
    /**
     * 锟斤拷锟斤拷锟斤拷锟藉布锟斤拷
     */
    private View ll_content;
    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷目选锟今布撅拷
     */
    private View ll_more;
    /**
     * 锟斤拷锟斤拷锟较讹拷锟斤拷锟斤拷锟斤拷
     */
    private View rl_column;
    /**
     * 锟斤拷锟斤拷影图片
     */
    private ImageView leftImage;
    /**
     * 锟斤拷锟斤拷影图片
     */
    private ImageView rightImage;
    /**
     * 锟斤拷幕锟斤拷锟?
     */
    private int mScreenWitdh = 0;
    /**
     * 锟斤拷锟斤拷幕疃痑ctivity
     */
    private Activity activity;

    public ColumnHorizontalScrollView(Context context) {
        super(context);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 锟斤拷锟较讹拷锟斤拷时锟斤拷执锟斤拷
     */
    @Override
    protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        // TODO Auto-generated method stub
        super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        shade_ShowOrHide();
        if (!activity.isFinishing() && ll_content != null && leftImage != null && rightImage != null && ll_more != null && rl_column != null) {
            if (ll_content.getWidth() <= mScreenWitdh) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        if (paramInt1 == 0) {
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        if (ll_content.getWidth() - paramInt1 + ll_more.getWidth() + rl_column.getLeft() == mScreenWitdh) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }

    /**
     * 锟斤拷锟诫父锟洁布锟斤拷锟叫碉拷锟斤拷源锟侥硷拷
     */
    public void setParam(Activity activity, int mScreenWitdh, View paramView1, ImageView paramView2, ImageView paramView3, View paramView4, View paramView5) {
        this.activity = activity;
        this.mScreenWitdh = mScreenWitdh;
        ll_content = paramView1;
        leftImage = paramView2;
        rightImage = paramView3;
        ll_more = paramView4;
        rl_column = paramView5;
    }

    /**
     * 锟叫讹拷锟斤拷锟斤拷锟斤拷影锟斤拷锟斤拷示锟斤拷锟斤拷效锟斤拷
     */
    public void shade_ShowOrHide() {
        if (!activity.isFinishing() && ll_content != null) {
            measure(0, 0);
            //锟斤拷锟斤拷锟斤拷锟斤拷锟叫★拷锟斤拷锟侥伙拷锟饺的伙拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷影锟斤拷锟斤拷锟斤拷
            if (mScreenWitdh >= getMeasuredWidth()) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷时锟斤拷锟斤拷锟斤拷锟接帮拷锟斤拷兀锟斤拷冶锟斤拷锟绞?
        if (getLeft() == 0) {
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        //锟斤拷锟斤拷锟斤拷锟斤拷冶锟绞憋拷锟斤拷锟斤拷锟斤拷影锟斤拷示锟斤拷锟揭憋拷锟斤拷锟斤拷
        if (getRight() == getMeasuredWidth() - mScreenWitdh) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        //锟斤拷锟斤拷说锟斤拷锟斤拷锟叫硷拷位锟矫ｏ拷锟斤拷锟斤拷锟斤拷影锟斤拷锟斤拷示
        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }
}
