package com.yishuqiao.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class ForResultNestedCompatFragment extends Fragment {

    private ForResultNestedCompatFragment forResultChildFragment;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof ForResultNestedCompatFragment) {
            ((ForResultNestedCompatFragment) parentFragment).startActivityForResultFromChildFragment(intent,
                    requestCode, this);
        } else {
            forResultChildFragment = null;
            super.startActivityForResult(intent, requestCode);
        }
    }

    private void startActivityForResultFromChildFragment(Intent intent, int requestCode,
                                                         ForResultNestedCompatFragment childFragment) {
        forResultChildFragment = childFragment;

        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof ForResultNestedCompatFragment) {
            ((ForResultNestedCompatFragment) parentFragment).startActivityForResultFromChildFragment(intent,
                    requestCode, this);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (forResultChildFragment != null) {
            forResultChildFragment.onActivityResult(requestCode, resultCode, data);
            forResultChildFragment = null;
        } else {
            onActivityResultNestedCompat(requestCode, resultCode, data);
        }
    }

    public void onActivityResultNestedCompat(int requestCode, int resultCode, Intent data) {

    }

}
