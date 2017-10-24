package com.yishuqiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class MyListview extends ListView {

	public MyListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	@SuppressWarnings("deprecation")
	public MyListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		@SuppressWarnings("unused")
		boolean a = super.onInterceptTouchEvent(ev);
		boolean b = mGestureDetector.onTouchEvent(ev);
		return b;
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (Math.abs(distanceY) / Math.abs(distanceX) > 2) {
				return true;
			}
			return false;
		}

	}

}
