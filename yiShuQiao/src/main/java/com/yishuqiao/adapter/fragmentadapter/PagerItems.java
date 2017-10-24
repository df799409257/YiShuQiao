package com.yishuqiao.adapter.fragmentadapter;

import java.util.ArrayList;

import android.content.Context;

@SuppressWarnings("serial")
public abstract class PagerItems<T extends PagerItem> extends ArrayList<T> {

    private final Context context;

    protected PagerItems(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

}