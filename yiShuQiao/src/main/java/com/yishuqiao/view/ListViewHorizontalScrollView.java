package com.yishuqiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ListViewHorizontalScrollView extends ListView {

    private float mDX, mDY, mLX, mLY;
    int mLastAct = -1;
    boolean mIntercept = false;

    public ListViewHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListViewHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewHorizontalScrollView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDX = mDY = 0f;
            mLX = ev.getX();
            mLY = ev.getY();

            break;

        case MotionEvent.ACTION_MOVE:
            final float curX = ev.getX();
            final float curY = ev.getY();
            mDX += Math.abs(curX - mLX);
            mDY += Math.abs(curY - mLY);
            mLX = curX;
            mLY = curY;

            if (mIntercept && mLastAct == MotionEvent.ACTION_MOVE) {
                return false;
            }

            if (mDX > mDY) {

                mIntercept = true;
                mLastAct = MotionEvent.ACTION_MOVE;
                return false;
            }

        }
        mLastAct = ev.getAction();
        mIntercept = false;
        return super.onInterceptTouchEvent(ev);
    }

}
